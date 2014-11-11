package models;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.plugin.RedisPlugin;
import play.libs.Akka;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.WebSocket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * A chat room is an Actor.
 */
public class ChatRoom extends UntypedActor {
    
    // Default room.
    private final String CHANNEL;
    private final String MEMBERS;

    public static Props props(final String eventId, final String instructor) {
        return Props.create(new Creator<ChatRoom>() {
            private static final long serialVersionUID = 1L;

            @Override
            public ChatRoom create() throws Exception {
                return new ChatRoom(eventId, instructor);
            }
        });
    }

    public ChatRoom(final String eventId, final String instructor){
        CHANNEL = instructor + ".event." + eventId;
        System.out.println(CHANNEL);
        MEMBERS = "members." + eventId;
        //add the robot
        //new Robot(getSelf());

        //subscribe to the message channel
        Akka.system().scheduler().scheduleOnce(
                Duration.create(10, TimeUnit.MILLISECONDS),
                new Runnable() {
                    public void run() {
                        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
                        j.subscribe(new MyListener(), CHANNEL);
                    }
                },
                Akka.system().dispatcher()
        );

        Akka.system().scheduler().scheduleOnce(
                Duration.create(10, TimeUnit.SECONDS),
                new Runnable() {
                    public void run() {
                        new Robot(getSelf());
                    }
                },
                Akka.system().dispatcher()
        );
    }
    
    /**
     * Join the default room.
     */
    
    private void remoteMessage(Object message) {
        getSelf().tell(message, null);
    }
    
    // Users connected to this node
    Map<String, ArrayList<WebSocket.Out<JsonNode>>> members = new HashMap<String, ArrayList<WebSocket.Out<JsonNode>>>();
    
    public void onReceive(Object message) throws Exception {
            Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
            try {
                if(message instanceof Join) {
                    // Received a Join message
                    Join join = (Join)message;
                    // Check if this username is free
                    // I may be able to be able to remove the if condition, because Sign in logic is present
                    //if(j.sismember(MEMBERS, join.username)) {
                    //    getSender().tell("This username is already used", getSelf());
                    //} else {
                        //Add the member to this node and the global roster
                        System.out.println("join.channel - " + join.channel);
                        System.out.println("join.channel.toString() - " + join.channel.toString());
                        System.out.println("join.channel.hashCode() - " + join.channel.hashCode());
                     //= members.containsKey(join.username)? members.get(join.getUsername()):new ArrayList<WebSocket.Out<JsonNode>>();

                    if(members.containsKey(join.username)){
                        members.get(join.getUsername()).add(join.channel);
                    }
                    else{
                        ArrayList<WebSocket.Out<JsonNode>> sockets = new ArrayList<WebSocket.Out<JsonNode>>();
                        sockets.add(join.channel);
                        members.put(join.getUsername(),sockets);
                    }

                        //members.put(join.username, join.channel);
                        j.sadd(MEMBERS, join.username);

                        //Publish the join notification to all nodes
                        RosterNotification rosterNotify = new RosterNotification(join.username, "join");
                        j.publish(CHANNEL, Json.stringify(Json.toJson(rosterNotify)));
                        getSender().tell("OK", getSelf());
                    //}

                } else if(message instanceof Quit)  {
                    // Received a Quit message
                    Quit quit = (Quit)message;
                    //Remove the member from this node and the global roster
                    members.get(quit.username).remove(quit.out);
                    j.srem(MEMBERS, quit.username);

                    //Publish the quit notification to all nodes
                    RosterNotification rosterNotify = new RosterNotification(quit.username, "quit");
                    j.publish(CHANNEL, Json.stringify(Json.toJson(rosterNotify)));
                } else if(message instanceof RosterNotification) {
                    //Received a roster notification
                    RosterNotification rosterNotify = (RosterNotification) message;
                    if("join".equals(rosterNotify.direction)) {
                        notifyAll("join", rosterNotify.username, "Hi, Anil this side. Doing my Masters in Computer Science at ASU. I like developing mobile apps!");
                    } else if("quit".equals(rosterNotify.direction)) {
                        notifyAll("quit", rosterNotify.username, "has left the room");
                    }
                } else if(message instanceof Talk)  {
                    // Received a Talk message
                    Talk talk = (Talk)message;
                    notifyAll("talk", talk.username, talk.text);

                } else {
                    unhandled(message);
                }
            } finally {
                play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
            }

    }
    
    // Send a Json event to all members connected to this node
    public void notifyAll(String kind, String user, String text) {
        for(ArrayList<WebSocket.Out<JsonNode>> channels: members.values()) {
            for(WebSocket.Out<JsonNode> channel: channels){
                ObjectNode event = Json.newObject();
                event.put("kind", kind);
                event.put("user", user);
                event.put("message", text);

                ArrayNode m = event.putArray("members");

                //Go to Redis to read the full roster of members. Push it down with the message.
                Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();
                try {
                    for(String u: j.smembers(MEMBERS)) {
                        m.add(u);
                    }
                } finally {
                    play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
                }

                channel.write(event);
            }
        }
    }
    
    // -- Messages
    
    public static class Join {
        
        final String username;
        final WebSocket.Out<JsonNode> channel;
        
        public String getUsername() {
			return username;
		}
        public String getType() {
        	return "join";
        }

		public Join(String username, WebSocket.Out<JsonNode> channel) {
            this.username = username;
            this.channel = channel;
        }
    }
    
    public static class RosterNotification {
    	
    	final String username;
    	final String direction;
    	
    	public String getUsername() {
    		return username;
    	}
    	public String getDirection() {
    		return direction;
    	}
    	public String getType() {
    		return "rosterNotify";
    	}
    	
    	public RosterNotification(String username, String direction) {
    		this.username = username;
    		this.direction = direction;
    	}
    }
    
    public static class Talk {
        
        final String username;
        final String eventId;
        final String text;
        
        public String getUsername() {
			return username;
		}
        public String getEventId() {
            return eventId;
        }
		public String getText() {
			return text;
		}
		public String getType() {
			return "talk";
		}

		public Talk(String username, String eventId, String text) {
            this.eventId = eventId;
            this.username = username;
            this.text = text;
        }
        
    }
    
    public static class Quit {
        
        final String username;
        final WebSocket.Out<JsonNode> out;
        
        public String getUsername() {
			return username;
		}
        public String getType() {
        	return "quit";
        }

		public Quit(String username, WebSocket.Out<JsonNode> out) {
            this.username = username;
            this.out = out;
        }
        
    }
    
    public class MyListener extends JedisPubSub {
		@Override
        public void onMessage(String channel, String messageBody) {
			//Process messages from the pub/sub channel
	    	JsonNode parsedMessage = Json.parse(messageBody);
	    	Object message = null;
	    	String messageType = parsedMessage.get("type").asText();
	    	if("talk".equals(messageType)) {	    		
	    		message = new Talk(
	    				parsedMessage.get("username").asText(),
                        parsedMessage.get("eventId").asText(),
	    				parsedMessage.get("text").asText()
	    				);
	    	} else if("rosterNotify".equals(messageType)) {	
	    		message = new RosterNotification(
	    				parsedMessage.get("username").asText(),
	    				parsedMessage.get("direction").asText()
	    				);
	    	}
			remoteMessage(message);
        }
		@Override
        public void onPMessage(String arg0, String arg1, String arg2) {
        }
		@Override
        public void onPSubscribe(String arg0, int arg1) {
        }
		@Override
        public void onPUnsubscribe(String arg0, int arg1) {
        }
		@Override
        public void onSubscribe(String arg0, int arg1) {
        }
		@Override
        public void onUnsubscribe(String arg0, int arg1) {
        }
    }
    
}
