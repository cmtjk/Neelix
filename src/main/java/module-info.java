module de.cmtjk.neelix {
	requires javafx.graphics;
	requires javafx.web;
	requires java.sql;
	requires org.apache.logging.log4j;
	requires org.apache.logging.log4j.core;
	requires mysql.connector.java;
	requires java.naming;

	exports de.cmtjk.neelix.main to javafx.graphics;

	opens de.cmtjk.neelix.model.resources to javafx.base;
}
