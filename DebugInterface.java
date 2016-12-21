package debug;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSON;

public class DebugInterface {

	/**
	 * 请求url
	 */
	String url;
	
	/**
	 * 必填参数列表
	 */
	List<Field> must = new ArrayList<>();
	
	/**
	 * 非必填参数列表
	 */
	List<Field> noMust = new ArrayList<>();
	
	/**
	 * 请求次数
	 */
	int num = 0;
	
	/**
	 * 请求接口
	 */
	public void run() {
		
		if(url == null){
			System.out.println("没有设置请求接口");
			return;
		}
		
		long start = System.currentTimeMillis();
		
		setDefult(must);
		setDefult(noMust);
		
		StringBuffer mustSB = new StringBuffer();
		if(must.size()>0){
			for(Field f : must){
				if(f != null){
					mustSB.append(f.getName());
					mustSB.append(":");
					mustSB.append(f.getDefault_value());
					mustSB.append(",");
				}
			}
		}
		String musts = mustSB.toString();
		
		List<Field[]> finish = new ArrayList<Field[]>();
		createArray(noMust, 0, new Field[0], finish);
		
		Iterator<Field[]> it = finish.iterator();
		while(it.hasNext()){
			Field[] fields = it.next();
			if(fields.length > 0){
				StringBuffer sb = new StringBuffer();
				sb.append("{");
				sb.append(musts);
				for(Field f : fields){
					if(f != null){
						sb.append(f.getName());
						sb.append(":");
						sb.append(f.getDefault_value());
						sb.append(",");
					}
				}
				sb.append("}");
				doPost(url, sb.toString());
				num++;
			}
		}
		
		doPost(url,"{"+musts+"}");
		num++;
		
		long end = System.currentTimeMillis();
		System.out.println("一共请求了"+num+"次接口，耗时"+(end - start)+"ms");
	}
	
	/**
	 * 执行post请求
	 * @param url
	 * @param param
	 */
	private void doPost(String url, String param){
		
		PrintWriter out = null;
		BufferedReader br = null;
		try{
			URL posturl = new URL(url);
			URLConnection con = posturl.openConnection();
			con.setDoOutput(true);
			con.setDoInput(true);
			out = new PrintWriter(con.getOutputStream());
			out.print(param);
			out.flush();
			
			br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer buf = new StringBuffer();
			String response;
			while((response = br.readLine()) != null){
				buf.append(response);
			}
			
			String s = buf.toString();
			HashMap obj = (HashMap) JSON.parseObject(s,HashMap.class);
			int code = (int) obj.get("errorCode");
			if(code != 0){
				String ss = "errorCode="+code+",参数为："+param;
				System.out.println(ss);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置请求url
	 * @param url
	 */
	public void setURL(String url){
		this.url = url;
	}
	
	/**
	 * 添加参数
	 * @param name
	 * @param type
	 * @param default_value
	 * @param ismust
	 */
	public void addField(String name, String type, String default_value, boolean ismust){
		
		Field f;
		if(default_value != null){
			f = new Field(name, type, default_value);
		}else{
			f = new Field(name, type);
		}
		if(ismust){
			must.add(f);
		}else{
			noMust.add(f);
		}
	}
	
	/**
	 * 设置参数的默认值
	 * @param fields
	 */
	private void setDefult(List<Field> fields){
		
		if(fields.size() <= 0)
			return;
		
		Field f;
		String type;
		Iterator<Field> it1 = fields.iterator();
		while(it1.hasNext()){
			f = it1.next();
			if(f.getDefault_value() != null)
				continue;
			type = f.getType();
			if(type.equalsIgnoreCase("strinng")){
				f.setDefault_value("stringValue");
			}else if(type.equalsIgnoreCase("int")){
				f.setDefault_value(String.valueOf((int)(1+Math.random()*10)));
			}else if(type.equalsIgnoreCase("datetime")){
				f.setDefault_value("2016-02-02");
			}
		}
	}
	
	/**
	 * 创建参数各种组合的集合
	 * @param fields
	 * @param index
	 * @param field_arr
	 * @param finish
	 */
	private void createArray(List<Field> fields, int index, Field[] field_arr, List<Field[]> finish){
		
		for(int i = index; i < fields.size(); i++){
			Field[] arr = new Field[field_arr.length + 1];
			System.arraycopy(field_arr, 0, arr, 0, field_arr.length);
			arr[field_arr.length] = (Field) fields.get(i);
			finish.add(arr);
			if(index < fields.size())
				createArray(fields, i+1, arr, finish);
		}
	}
}



class Field{
	
	String name;
	String type;
	String default_value;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDefault_value() {
		return default_value;
	}

	public void setDefault_value(String default_value) {
		this.default_value = default_value;
	}

	public Field(String name, String type, String default_value) {
		this.name = name;
		this.type = type;
		this.default_value = default_value;
	}
	
	public Field(String name, String type) {
		this.name = name;
		this.type = type;
		this.default_value = null;
	}
	
	@Override
	public String toString() {
		
		return this.name;
	}
}
