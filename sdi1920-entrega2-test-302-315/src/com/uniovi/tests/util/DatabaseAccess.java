package com.uniovi.tests.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class DatabaseAccess {
	
	static MongoClient mongoClient;

	private static MongoDatabase getDatabase() {
		MongoClientURI uri = new MongoClientURI(
				"mongodb://admin:arHPmEhl6Y764gyd@socialnetwork-shard-00-00-ezgy1.mongodb.net:27017,socialnetwork-shard-00-01-ezgy1.mongodb.net:27017,socialnetwork-shard-00-02-ezgy1.mongodb.net:27017/test?ssl=true&replicaSet=SocialNetwork-shard-0&authSource=admin&retryWrites=true&w=majority");

		mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase("test");
		return database;
	}
	
	public static void closeDatabase() {
		mongoClient.close();
	}
	
	public static String getUserIdFromEmail(String email) {
		MongoDatabase db = getDatabase();
		MongoCollection<Document> collection = db.getCollection("users");
		Bson bsonFilter = Filters.eq("email", email);
		FindIterable<Document> docs = collection.find(bsonFilter);
		for (Document doc : docs) {
			return doc.get("_id").toString();
		}
		return null;
	}
	
	public static void removeUser(String email) {
		MongoDatabase db = getDatabase();
		MongoCollection<Document> collection = db.getCollection("users");
		Bson bsonFilter = Filters.eq("email", email);
		collection.deleteMany(bsonFilter);
	}
	
	public static int getNumberOfUsers() {
		MongoDatabase db = getDatabase();
		MongoCollection<Document> collection = db.getCollection("users");
		return (int)collection.count();
	}
	
	public static int getNumberOfFriendsOfUser(String email) {
		MongoDatabase db = getDatabase();
		MongoCollection<Document> collection = db.getCollection("users");
		Bson bsonEmail = Filters.eq("email", email);
		FindIterable<Document> docs = collection.find(bsonEmail);
		for (Document doc : docs) {
			String id = String.valueOf(doc.get("_id"));
			Bson bsonId = Filters.eq("userFrom", new ObjectId(id));
			MongoCollection<Document> collectionAmigos = db.getCollection("friendships");
			return (int) collectionAmigos.count(bsonId);
		}
		return -1;
	}

	public static void createFriendship(String from, String to, boolean accepted) {
		MongoDatabase db = getDatabase();
		MongoCollection<Document> collection = db.getCollection("friendships");
		List<Document> docs = new ArrayList<>();
		
		Document doc = new Document();
			doc.append("userTo", new ObjectId(to));
			doc.append("userFrom", new ObjectId(from));
			doc.append("accepted", accepted);
		docs.add(doc);
		if (accepted) {
			Document otherWay = new Document();
				otherWay.append("userTo", new ObjectId(from));
				otherWay.append("userFrom", new ObjectId(to));
				otherWay.append("accepted", accepted);
			docs.add(otherWay);
		}
		collection.insertMany(docs);
	}
	
	public static void removeFriendship(String from, String to) {
		MongoDatabase db = getDatabase();
		MongoCollection<Document> collection = db.getCollection("friendships");
		Bson bsonFilter = 
				Filters.or(
					Filters.and( // One way
						Filters.eq("userFrom", new ObjectId(from)), 
						Filters.eq("userTo", new ObjectId(to))
					),
					Filters.and( // The other way
							Filters.eq("userFrom", new ObjectId(to)), 
							Filters.eq("userTo", new ObjectId(from))
					)
				);
		collection.deleteMany(bsonFilter);
	}
	
	public static void removeMessage(String from, String to) {
		MongoDatabase db = getDatabase();
		MongoCollection<Document> collection = db.getCollection("mensajes");
		Bson bsonFilter = 
				Filters.or(
					Filters.and( // One way
						Filters.eq("origen", (from)), 
						Filters.eq("userdestino", (to))
					),
					Filters.and( // The other way
							Filters.eq("origen", (to)), 
							Filters.eq("destino", (from))
					)
				);
		collection.deleteMany(bsonFilter);
	}

	public static void writeMessage(String emisor, String destino, String text, boolean leido) {
		MongoDatabase db = getDatabase();
		MongoCollection<Document> collection = db.getCollection("mensajes");
		
		Document doc = new Document();
			doc.append("emisor", destino);
			doc.append("destino", emisor);
			doc.append("texto", text);
			doc.append("leido", leido);
			doc.append("fecha", new Date());
		collection.insertOne(doc);
	}
	
	public static int getNumberOfNonReadedMessages(String destino) {
		MongoDatabase db = getDatabase();
		MongoCollection<Document> collection = db.getCollection("mensajes");
		Bson bsonFilter = Filters.and(
								Filters.eq("destino", destino),
								Filters.eq("leido", false)
						  );
		return (int) collection.count(bsonFilter);
	}
}
