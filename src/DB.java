import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Stack;


public class DB {

	/*Storage objects*/
	static Hashtable<String,String> db=new Hashtable<String, String>();//Persistent Storage
	static Hashtable<String,Stack<String>> txn_db=new Hashtable<String,Stack<String>>();//transaction storage, temporary
	static Stack<String> txn_stack= new Stack<String>();//for nested transactions
	static int txn_number=0;
	
	/*Scan the input commands*/
	private static Scanner input;
	
	public static void main(String [] args){
		input=new Scanner(System.in);
		
		/*Scan the input commands*/
		while(input.hasNext()){
			String [] cmd=input.nextLine().split(" ");
			if(cmd[0].equals("SET")) setHandler(cmd[1],cmd[2]);
			else if(cmd[0].equals("UNSET")) unsetHandler(cmd[1]);
			else if(cmd[0].equals("GET")) getHandler(cmd[1]);
			else if(cmd[0].equals("BEGIN")) begintHandler();
			else if(cmd[0].equals("COMMIT")) commitHandler();
			else if(cmd[0].equals("ROLLBACK")) rollbackHandler();
			else if(cmd[0].equals("NUMEQUALTO")) numequaltoHandler(cmd[1]);
			else if(cmd[0].equals("TXNDB")) System.out.println(txn_db);
			else if(cmd[0].equals("NDB")) System.out.println(db);
			
		}
	}

	
	private static void setHandler(String variable, String value) {
		// TODO Auto-generated method stub
		/*case 1 transaction is in progress*/
		if(txn_number>0){
			txn_stack.push(variable);
			if(txn_db.containsKey(variable)){
				txn_db.get(variable).push(value);
			}
			else {
				Stack<String> st=new Stack<String>();
			    st.push(value);
			    txn_db.put(variable, st);
			}
			
		}
		/*case 2 whene there is not transaction in progress*/
		else db.put(variable, value);
	}
	
	private static void unsetHandler(String variable) {
		// TODO Auto-generated method stub
		if(txn_number>0){
			txn_stack.push(variable);
			if(txn_db.containsKey(variable)){
				txn_db.get(variable).push(null);
			}
			else {
				Stack<String> st=new Stack<String>();
			    st.push(null);
			    txn_db.put(variable, st);
			}
		}
		else db.put(variable,null);
				
	}

	private static void getHandler(String variable) {
		// TODO Auto-generated method stub
		if(txn_db.containsKey(variable)){
			System.out.println(txn_db.get(variable).peek());
		}
		else System.out.println(db.get(variable));
	}
	
	private static void begintHandler() {
		// TODO Auto-generated method stub
		txn_stack.push("#");
		txn_number=txn_number+1;
	}
	
	private static void commitHandler() {
		// TODO Auto-generated method stub
		if(txn_number==0)System.out.println("NO TRANSACTION");
		else {
			txn_number=0;
			for(String key : txn_db.keySet()){
				db.put(key, txn_db.get(key).peek());
			}
			txn_db.clear();
		}
		
	}
	
	private static void rollbackHandler() {
		// TODO Auto-generated method stub
		if(txn_number==0)System.out.println("NO TRANSACTION");
		else {
			txn_number=txn_number-1;
			if(txn_db.size()>0){
				String sentinel=txn_stack.pop();
				while(sentinel.equals("#")==false){
					txn_db.get(sentinel).pop();
					if(txn_db.get(sentinel).size()==0){
						txn_db.remove(sentinel);
					}
					sentinel=txn_stack.pop();
				}	
			}
			
		}
	}
	
	private static void numequaltoHandler(String value) {
		// TODO Auto-generated method stub
		int count=0;{
			/*count the elements in txn_db*/
			for(String key: txn_db.keySet()){
				if(txn_db.get(key).peek()!=null){
					if(txn_db.get(key).peek().equals("10")) count++;	
				}
				
			}
			
			/*count the elements in persistent store make sure we dont count the variable that is der in txn stack*/
			for(String key: db.keySet()){
				if(txn_db.containsKey(key)==false && db.get(key).equals("10") ) count++; 
			}
			
			System.out.println(count);
		}
		
	}

}
