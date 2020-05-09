package com.uniovi.tests.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class DatabaseAccess {

	private static MongoDatabase getDatabase() {
		MongoClientURI uri = new MongoClientURI(
				"mongodb://admin:<password>@socialnetwork-shard-00-00-ezgy1.mongodb.net:27017,socialnetwork-shard-00-01-ezgy1.mongodb.net:27017,socialnetwork-shard-00-02-ezgy1.mongodb.net:27017/test?ssl=true&replicaSet=SocialNetwork-shard-0&authSource=admin&retryWrites=true&w=majority");

		MongoClient mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase("test");
		return database;
	}

}
