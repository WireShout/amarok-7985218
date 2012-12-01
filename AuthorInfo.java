import java.util.ArrayList;

public class AuthorInfo {
	private String email;
	private int count;
	private ArrayList<Integer> years = new ArrayList<Integer>();
	
	public AuthorInfo(String e) {
		email = e.split(";;;")[0];
		if(e.split(";;;").length == 2)
			years.add(Integer.parseInt(e.split(";;;")[1]));
		count = 1;
	}
	public String getEmail() {
		return email;
	}
	public void addFile(String file) {
		count++;
	}
	public int getNumOfFiles() {
		return count;
	}
	public void addYear(int y){
		boolean found = false;
		for(int i:years) {
			if(i == y)
				found = true;
		}
		if(!found)
			years.add(y);
	}
	public String getCopyrightYears() {
		String returnVal = "";
		for(int i:years) {
			returnVal += i + " ";
		}
		return returnVal;
	}
}