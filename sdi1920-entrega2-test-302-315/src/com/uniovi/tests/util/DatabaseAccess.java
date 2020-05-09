package com.uniovi.tests.util;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class DatabaseAccess {
	
	static MongoClient mongoClient;

	private static MongoDatabase getDatabase() {
		MongoClientURI uri = new MongoClientURI(
				"mongodb://admin:arHPmEhl6Y764gyd@socialnetwork-shard-00-00-ezgy1.mongodb.net:27017,socialnetwork-shard-00-01-ezgy1.mongodb.net:27017,socialnetwork-shard-00-02-ezgy1.mongodb.net:27017/test?ssl=true&replicaSet=SocialNetwork-shard-0&authSource=admin&retryWrites=true&w=majority");

		mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase("SocialNetwork");
		return database;
	}
	
	public static void closeDatabase() {
		mongoClient.close();
	}
	
	public static void removeUser(String email) {
		MongoDatabase db = getDatabase();
		MongoCollection<Document> collection = db.getCollection( "users" );
		Bson bsonFilter = Filters.eq("email", email);
		collection.deleteOne(bsonFilter);
	}

}
