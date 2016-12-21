package debug;

public class demo {

	public static void main(String[] args) {
		
		DebugInterface di = new DebugInterface();
		
		di.addField("payStatus", "int", "1", true);
		di.addField("overdueType", "int", "1", true);
		di.addField("phoneNo", "int", null, true);
		di.addField("insuredName", "int", null, true);
		di.addField("carLicenseNO", "int", null, true);
		di.addField("pageNumber", "int", null, true);
		di.addField("pageSize", "int", null, false);
		di.addField("sortDir", "int", "DESC", false);
		
		di.setURL("http://127.0.0.1:8080/jsse/billManager/listBillForUser");
		
		di.run();
		
	}
}
