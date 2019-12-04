package com.merck.utils;

import static org.apache.poi.xssf.usermodel.XSSFRelation.NS_SPREADSHEETML;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 自定义类，主要重写ReadOnlySharedStringsTable类相关方法（getEntryAt，endElement方法），
 * 解决读取sharedStrings.xml时候，内存使用过多导致内存溢出。
 * 优化方式主要是讲单元格字符串拼接成固定长度字符串存储，然后按照一定顺序取出。
 */
public class MyShardingTable extends DefaultHandler {
	
	private int num=10;//list的每个位置存放多少值

	  private final boolean includePhoneticRuns;
	    /**
	     * An integer representing the total count of strings in the workbook. This count does not
	     * include any numbers, it counts only the total of text strings in the workbook.
	     */
	    private int count;

	    /**
	     * An integer representing the total count of unique strings in the Shared String Table.
	     * A string is unique even if it is a copy of another string, but has different formatting applied
	     * at the character level.
	     */
	    private int uniqueCount;

	    /**
	     * The shared strings table.
	     */
	    private List<String> strings;

	    /**
	     * Map of phonetic strings (if they exist) indexed
	     * with the integer matching the index in strings
	     */
	    private Map<Integer, String> phoneticStrings;

	    /**
	     * Calls {{@link #(OPCPackage, boolean)}} with
	     * a value of <code>true</code> for including phonetic runs
	     *
	     * @param pkg The {@link OPCPackage} to use as basis for the shared-strings table.
	     * @throws IOException If reading the data from the package fails.
	     * @throws SAXException if parsing the XML data fails.
	     */
	    public MyShardingTable(OPCPackage pkg)
	            throws IOException, SAXException {
	        this(pkg, true,null);
	    }
	    
	    public MyShardingTable(OPCPackage pkg, Integer num)
	            throws IOException, SAXException {
	    	this(pkg, true,num);
	    }

	    /**
	     *
	     * @param pkg The {@link OPCPackage} to use as basis for the shared-strings table.
	     * @param includePhoneticRuns whether or not to concatenate phoneticRuns onto the shared string
	     * @since POI 3.14-Beta3
	     * @throws IOException If reading the data from the package fails.
	     * @throws SAXException if parsing the XML data fails.
	     */
	    public MyShardingTable(OPCPackage pkg, boolean includePhoneticRuns, Integer num)
	            throws IOException, SAXException {
	    	if(num!=null&&num!=0){
	    		this.num=num;
	    	}
	        this.includePhoneticRuns = includePhoneticRuns;
	        ArrayList<PackagePart> parts =
	                pkg.getPartsByContentType(XSSFRelation.SHARED_STRINGS.getContentType());

	        // Some workbooks have no shared strings table.
	        if (parts.size() > 0) {
	            PackagePart sstPart = parts.get(0);
	            readFrom(sstPart.getInputStream());
	        }
	    }

	    /**
	     * Like POIXMLDocumentPart constructor
	     *
	     * Calls {@link #(PackagePart, boolean)}, with a
	     * value of <code>true</code> to include phonetic runs.
	     *
	     * @since POI 3.14-Beta1
	     */
	    public MyShardingTable(PackagePart part) throws IOException, SAXException {
	        this(part, true,null);
	    }
	    public MyShardingTable(PackagePart part, Integer num) throws IOException, SAXException {
	    	this(part, true,num);
	    }

	    /**
	     * Like POIXMLDocumentPart constructor
	     *
	     * @since POI 3.14-Beta3
	     */
	    public MyShardingTable(PackagePart part, boolean includePhoneticRuns, Integer num)
	        throws IOException, SAXException {
	    	if(num!=null&&num!=0){
	    		this.num=num;
	    	}
	        this.includePhoneticRuns = includePhoneticRuns;
	        readFrom(part.getInputStream());
	    }
	    
	    /**
	     * Read this shared strings table from an XML file.
	     *
	     * @param is The input stream containing the XML document.
	     * @throws IOException if an error occurs while reading.
	     * @throws SAXException if parsing the XML data fails.
	     */
	    public void readFrom(InputStream is) throws IOException, SAXException {
	        // test if the file is empty, otherwise parse it
	        PushbackInputStream pis = new PushbackInputStream(is, 1);
	        int emptyTest = pis.read();
	        if (emptyTest > -1) {
	            pis.unread(emptyTest);
	            InputSource sheetSource = new InputSource(pis);
	            try {
	                XMLReader sheetParser = SAXHelper.newXMLReader();
	                sheetParser.setContentHandler(this);
	                sheetParser.parse(sheetSource);
	            } catch(ParserConfigurationException e) {
	                throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
	            }
//	            finally {
////						is.close();
//				}
	        }
	    }

	    /**
	     * Return an integer representing the total count of strings in the workbook. This count does not
	     * include any numbers, it counts only the total of text strings in the workbook.
	     *
	     * @return the total count of strings in the workbook
	     */
	    public int getCount() {
	        return this.count;
	    }

	    /**
	     * Returns an integer representing the total count of unique strings in the Shared String Table.
	     * A string is unique even if it is a copy of another string, but has different formatting applied
	     * at the character level.
	     *
	     * @return the total count of unique strings in the workbook
	     */
	    public int getUniqueCount() {
	        return this.uniqueCount;
	    }

	    /**
	     * Return the string at a given index.
	     * Formatting is ignored.
	     *
	     * @param idx index of item to return.
	     * @return the item at the specified position in this Shared String table.
	     */
	    public String getEntryAt(int idx) {
	    	int s=idx/num;
	    	String str=strings.get(s);
	    	List<String> d=Arrays.asList(str.split("#`@"));
	    	return d.get(idx-s*num);

			//  return strings.get(idx);
	    }

	    public List<String> getItems() {
	        return strings;
	    }

	    //// ContentHandler methods ////

	    private StringBuffer characters;
	    private boolean tIsOpen;
	    private boolean inRPh;
	    private int countNum=1;
	    private StringBuilder build;

	    public void startElement(String uri, String localName, String name,
	                             Attributes attributes) throws SAXException {
	        if (uri != null && ! uri.equals(NS_SPREADSHEETML)) {
	            return;
	        }

	        if ("sst".equals(localName)) {
	            String count = attributes.getValue("count");
	            if(count != null) this.count = Integer.parseInt(count);
	            String uniqueCount = attributes.getValue("uniqueCount");
	            if(uniqueCount != null) this.uniqueCount = Integer.parseInt(uniqueCount);

	            this.strings = new ArrayList<String>(this.uniqueCount);
	            this.phoneticStrings = new HashMap<Integer, String>();
	            characters = new StringBuffer();
	        } else if ("si".equals(localName)) {
	            characters.setLength(0);
	        } else if ("t".equals(localName)) {
	            tIsOpen = true;
	        } else if ("rPh".equals(localName)) {
	            inRPh = true;
	            //append space...this assumes that rPh always comes after regular <t>
	            if (includePhoneticRuns && characters.length() > 0) {
	                characters.append(" ");
	            }
	        }
	    }

	    public void endElement(String uri, String localName, String name)
	            throws SAXException {
	        if (uri != null && ! uri.equals(NS_SPREADSHEETML)) {
	            return;
	        }

	        if ("si".equals(localName)) {
	        	if(build==null){
	        		this.build=new StringBuilder(500);
	        	}
	        	
	        	build.append("#`@"+characters.toString());
	        	String s="";
	        	if(countNum%num==0||countNum==uniqueCount&&countNum%num!=0){
	        		s=build.toString();
	        		if(StringUtils.isNotEmpty(s)&&s.startsWith("#`@")){
	        			s=s.substring(3, s.length());
	        		}
	        		//strings.add(characters.toString());
	        		strings.add(s);
	        		build=null;
	        	}
//	        	if(countNum==uniqueCount&&countNum%num!=0){
//                    s=build.toString();
//	        		if(StringUtils.isNotEmpty(s)&&s.startsWith("#@")){
//	        			s=s.substring(2, s.length());
//	        		}
//	        		strings.add(s);
//	        	}
	        	countNum++;
				System.out.println(countNum);
	        } else if ("t".equals(localName)) {
	            tIsOpen = false;
	        } else if ("rPh".equals(localName)) {
	            inRPh = false;
	        }
	    }

	    /**
	     * Captures characters only if a t(ext) element is open.
	     */
	    public void characters(char[] ch, int start, int length)
	            throws SAXException {
	        if (tIsOpen) {
	            if (inRPh && includePhoneticRuns) {
	                characters.append(ch, start, length);
	            } else if (! inRPh){
	                characters.append(ch, start, length);
	            }
	        }
	    }
}
