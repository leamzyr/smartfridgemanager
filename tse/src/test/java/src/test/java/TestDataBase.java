package src.test.java;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import app.tse.DataBase;

public class TestDataBase {

	   private DataBase BDD;

	    @Before
	    public void initObjects() {
	        this.BDD = new DataBase("hihihaha.db");
			BDD.connectDatabase();
	    }
	    
		@After
		public void closeObjects() {
			this.BDD.close();
		}
	    
		@Test
		public void testVerifyTables() {
			this.BDD.verifyTables();
		}


}
