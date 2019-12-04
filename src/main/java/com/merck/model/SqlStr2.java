package com.merck.model;

public class SqlStr2 {
	/**
	 * opition2的逻辑
	 */

	public static String detail_Block = "  select tt.*,         \r\n" + 
			"			 			 case when cast(isnull(tt.credit_value,'0') as numeric(20,2)) <1000000         \r\n" + 
			"			 			          then  'Credit Management Team'\r\n" + 
			"                  when  1000000 <=cast(isnull(tt.credit_value,'0') as numeric(20,2))   and cast(isnull(tt.credit_value,'0') as numeric(20,2))<2000000\r\n" + 
			"			 			            then 'Sales Head & Credit Management Head'\r\n" + 
			"			 			      else 'Steve Vermant &    CFO'         \r\n" + 
			"			 			               \r\n" + 
			"			 			      end ORDER_RELEASE_MATRIX ,         \r\n" + 
			"			 			   REPLACE(CONVERT(VARCHAR(30),ROUND(tt.Deviation*100, 0)) + '%', '.000000', '') DEVIATION_PERCENT         \r\n" + 
			"			 			          \r\n" + 
			"			 			          \r\n" + 
			"			 			 from(         \r\n" + 
			"			 			 select t.*,         \r\n" + 
			"			 			 case when cast(REPLACE(ISNULL(t.CREDIT_LIMIT, '0'), ',', '') as NUMERIC(20,2))=0.00          \r\n" + 
			"			 			 then null          \r\n" + 
			"			 			 else  cast((t.overruns/t.CREDIT_LIMIT) as NUMERIC(20,6))         \r\n" + 
			"			 			 end DEVIATION         \r\n" + 
			"			 			  from (         \r\n" + 
			"			 			          \r\n" + 
			"			 			 select DISTINCT          \r\n" + 
			"			 			 a.DOCUMENT_NUMBER,a.PARTNER,rtrim(substring(a.DESCRIPTION,0,(charindex('/',a.DESCRIPTION)))) DESCRIPTION,a.TOTAL_AMT CREDIT_VALUE,a.EXTERNAL_REFER ,a.RISK_CLASS RISK,b.CREDIT_LIMIT,e.CC CC,a.CR_EXPOS EXPOSURE,         \r\n" +
			"			 			 cast(REPLACE(ISNULL(a.CR_EXPOS, '0'), ',', '') as NUMERIC(20,2))-cast(REPLACE(ISNULL(c.TOTAL_AR, '0'), ',', '') as NUMERIC(20,2)) OPEN_ORDER,         \r\n" + 
			"			 			 cast(REPLACE(ISNULL(a.CR_EXPOS, '0'), ',', '') as NUMERIC(20,2))-cast(REPLACE(ISNULL(b.CREDIT_LIMIT, '0'), ',', '') as NUMERIC(20,2)) OVERRUNS,         \r\n" + 
			"			 			 c.TOTAL_AR RECEIVABLES, REPLACE(c.DAYS_30_M, '- 0', '0') DAYS_30_M  ,d.R_OR_B ,e.SALES ,e.BUINESS_HEAD,e.SALES_HEAD HEAD,e.NAME1 ,f.CUSTOMER_BEHAVIOR_SCORING RATING         \r\n" + 
			"			 			 from BACK_ORDER_43 a         \r\n" + 
			"			 			 left join          \r\n" + 
			"			 			 CREDIT_LIMIT b on a.PARTNER=b.PARTNER         \r\n" + 
			"			 			 LEFT JOIN ZCOP_AGING c on a.PARTNER=c.CUSTOMER         \r\n" + 
			"			 			 LEFT JOIN ORDERING d on a.PARTNER=d.CUSTOMER         \r\n" + 
			"			 			 LEFT JOIN (         \r\n" + 
			"			 			 select t1.CC,t1.PAYER PAYER,t1.SALES SALES,t2.SALES_HEAD SALES_HEAD,t2.BUINESS_HEAD BUINESS_HEAD,t1.NAME1         \r\n" +
			"			 			 from PY_SALES_MAPPING t1          \r\n" + 
			"			 			 LEFT JOIN SIGMA t2 on t1.SALES=t2.SALES         \r\n" + 
			"			 			 ) e on a.PARTNER=e.PAYER         \r\n" + 
			"			 			 LEFT JOIN SCORING f on a.PARTNER=f.PAYER         \r\n" + 
			"			 			 where ( R_OR_B ='block' or R_OR_B ='blocked' ) and a.payT!='ppd' )t)tt ;";
	public static String detail_Release = "select tt.*,         \r\n" + 
			"			 			  case when cast(isnull(tt.credit_value,'0') as numeric(20,2)) <1000000         \r\n" + 
			"			 			          then  'Credit Management Team'\r\n" + 
			"                  when  1000000 <=cast(isnull(tt.credit_value,'0') as numeric(20,2))   and cast(isnull(tt.credit_value,'0') as numeric(20,2))<2000000\r\n" + 
			"			 			            then 'Sales Head & Credit Management Head'\r\n" + 
			"			 			      else 'Steve Vermant &    CFO'         \r\n" + 
			"			 			               \r\n" + 
			"			 			      end ORDER_RELEASE_MATRIX ,         \r\n" + 
			"			 			   REPLACE(CONVERT(VARCHAR(30),ROUND(tt.Deviation*100, 0)) + '%', '.000000', '') DEVIATION_PERCENT         \r\n" + 
			"			 			          \r\n" + 
			"			 			          \r\n" + 
			"			 			 from(         \r\n" + 
			"			 			 select t.*,         \r\n" + 
			"			 			 case when cast(REPLACE(ISNULL(t.CREDIT_LIMIT, '0'), ',', '') as NUMERIC(20,2))=0.00          \r\n" + 
			"			 			 then null          \r\n" + 
			"			 			 else  cast((t.overruns/t.CREDIT_LIMIT) as NUMERIC(20,6))         \r\n" + 
			"			 			 end DEVIATION         \r\n" + 
			"			 			  from (         \r\n" + 
			"			 			          \r\n" + 
			"			 			 select DISTINCT          \r\n" + 
			"			 			 a.DOCUMENT_NUMBER,a.PARTNER,rtrim(substring(a.DESCRIPTION,0,(charindex('/',a.DESCRIPTION)))) DESCRIPTION,a.TOTAL_AMT CREDIT_VALUE,a.EXTERNAL_REFER ,a.RISK_CLASS RISK,b.CREDIT_LIMIT,e.CC CC,a.CR_EXPOS EXPOSURE,         \r\n" +
			"			 			 cast(REPLACE(ISNULL(a.CR_EXPOS, '0'), ',', '') as NUMERIC(20,2))-cast(REPLACE(ISNULL(c.TOTAL_AR, '0'), ',', '') as NUMERIC(20,2)) OPEN_ORDER,         \r\n" + 
			"			 			 cast(REPLACE(ISNULL(a.CR_EXPOS, '0'), ',', '') as NUMERIC(20,2))-cast(REPLACE(ISNULL(b.CREDIT_LIMIT, '0'), ',', '') as NUMERIC(20,2)) OVERRUNS,         \r\n" + 
			"			 			 c.TOTAL_AR RECEIVABLES, REPLACE(c.DAYS_30_M, '- 0', '0') DAYS_30_M  ,d.R_OR_B ,e.SALES ,e.BUINESS_HEAD,e.SALES_HEAD HEAD,e.NAME1 ,f.CUSTOMER_BEHAVIOR_SCORING RATING         \r\n" + 
			"			 			 from BACK_ORDER_43 a         \r\n" + 
			"			 			 left join          \r\n" + 
			"			 			 CREDIT_LIMIT b on a.PARTNER=b.PARTNER         \r\n" + 
			"			 			 LEFT JOIN ZCOP_AGING c on a.PARTNER=c.CUSTOMER         \r\n" + 
			"			 			 LEFT JOIN ORDERING d on a.PARTNER=d.CUSTOMER         \r\n" + 
			"			 			 LEFT JOIN (         \r\n" + 
			"			 			 select t1.CC, t1.PAYER PAYER,t1.SALES SALES,t2.SALES_HEAD SALES_HEAD,t2.BUINESS_HEAD BUINESS_HEAD,t1.NAME1         \r\n" +
			"			 			 from PY_SALES_MAPPING t1          \r\n" + 
			"			 			 LEFT JOIN SIGMA t2 on t1.SALES=t2.SALES         \r\n" + 
			"			 			 ) e on a.PARTNER=e.PAYER         \r\n" + 
			"			 			 LEFT JOIN SCORING f on a.PARTNER=f.PAYER         \r\n" + 
			"			 			 where (R_OR_B ='Release' or R_OR_B ='Released') and a.payT!='ppd')t)tt WHERE not EXISTS (SELECT 1 FROM T_RELEASE_ORDER_EXCLUDE_PO WHERE PO=tt.document_number);";

	public static String No_Bill_of_Lading_Opinion = "SELECT\r\n" + "	*\r\n" + "FROM\r\n" + "	(\r\n"
			+ "		SELECT DISTINCT\r\n" + "			a.DOCUMENT_NUMBER,\r\n" + "			a.PARTNER,\r\n"
			+ "			e.cc CC,\r\n" + "			rtrim(substring(a.DESCRIPTION,0,(charindex('/',a.DESCRIPTION)))) NAME1\r\n" + "		FROM\r\n"
			+ "			BACK_ORDER_43 a\r\n" + "		LEFT JOIN CREDIT_LIMIT b ON a.PARTNER = b.PARTNER\r\n"
			+ "		LEFT JOIN ZCOP_AGING c ON a.PARTNER = c.CUSTOMER\r\n"
			+ "			   LEFT JOIN ORDERING d on a.PARTNER=d.CUSTOMER     LEFT JOIN ( \r\n"
			+ "			   select t1.CC,t1.PAYER PAYER,t1.SALES SALES,t2.SALES_HEAD SALES_HEAD,t2.BUINESS_HEAD BUINESS_HEAD,t1.NAME1 \r\n"
			+ "			   from PY_SALES_MAPPING t1      LEFT JOIN SIGMA t2 on t1.SALES=t2.SALES \r\n"
			+ "			   ) e on a.PARTNER=e.PAYER     LEFT JOIN SCORING f on a.PARTNER=f.PAYER  where  a.payT!='ppd'    ) fin  \r\n"
			+ "			   where not EXISTS (select 1 from ORDERING t4 where fin.PARTNER=t4.CUSTOMER) and len(fin.DOCUMENT_NUMBER)!=0 and fin.DOCUMENT_NUMBER is not null \r\n"
			+ "			    and NAME1 is not null order by CC asc , PARTNER asc ";

	public static String No_Debt_Card = "  select *       from (      select DISTINCT   \r\n" + 
			"			   a.DOCUMENT_NUMBER,a.PARTNER,e.cc CC,rtrim(substring(a.DESCRIPTION,0,(charindex('/',a.DESCRIPTION)))) NAME1,  REPLACE(DAYS_30_M, '- 0', '0')DAYS_30_M       from BACK_ORDER_43 a  \r\n" +
			"			   left join       CREDIT_LIMIT b on a.PARTNER=b.PARTNER  \r\n" + 
			"			   LEFT JOIN ZCOP_AGING c on a.PARTNER=c.CUSTOMER      LEFT JOIN ORDERING d on a.PARTNER=d.CUSTOMER  \r\n" + 
			"			   LEFT JOIN (  \r\n" + 
			"			   select t1.CC,t1.PAYER PAYER,t1.SALES SALES,t2.SALES_HEAD SALES_HEAD,t2.BUINESS_HEAD BUINESS_HEAD,t1.NAME1  \r\n" +
			"			   from PY_SALES_MAPPING t1       LEFT JOIN SIGMA t2 on t1.SALES=t2.SALES  \r\n" + 
			"			   ) e on a.PARTNER=e.PAYER      LEFT JOIN SCORING f on a.PARTNER=f.PAYER  \r\n" + 
			"			   where (R_OR_B='block' or R_OR_B='blocked' ) and a.payT!='ppd' )t \r\n" + 
			" where cast(REPLACE(t.DAYS_30_M, ',', '') as NUMERIC(20,2))<100 order by CC asc , PARTNER asc ;";

	public static final String order_release=" select   PARTNER ,       \r\n" + 
			"			 		  DESCRIPTION ,       \r\n" + 
			"			 		  HEAD ,       \r\n" + 
			"			 		  RATING ,       \r\n" + 
			"			 		   RECEIVABLES ,       \r\n" + 
			"			 		        \r\n" + 
			"			 		  isnull(DAYS_30_M,0)DAYS_30_M ,       \r\n" + 
			"			 		  OPEN_ORDER ,       \r\n" + 
			"			 		  CREDIT_LIMIT ,       \r\n" + 
			"			 		  EXPOSURE ,       \r\n" + 
			"			 		  OVERRUNS ,       \r\n" + 
			"			 		  DEVIATION_PERCENT,sum(cast(CREDIT_VALUE as NUMERIC(20,2))) CREDIT_VALUE  from(     \r\n" + 
			"			      \r\n" + 
			"			 select tt.*,         \r\n" + 
			"			 			case when cast(isnull(tt.credit_value,'0') as numeric(20,2)) <1000000         \r\n" + 
			"			 			          then  'Credit Management Team'\r\n" + 
			"                  when  1000000 <=cast(isnull(tt.credit_value,'0') as numeric(20,2))   and cast(isnull(tt.credit_value,'0') as numeric(20,2))<2000000\r\n" + 
			"			 			            then 'Sales Head & Credit Management Head'\r\n" + 
			"			 			      else 'Steve Vermant &    CFO'         \r\n" + 
			"			 			               \r\n" + 
			"			 			      end ORDER_RELEASE_MATRIX ,  \r\n" + 
			"			 			   REPLACE(CONVERT(VARCHAR(30),ROUND(tt.Deviation*100, 0)) + '%', '.000000', '') DEVIATION_PERCENT         \r\n" + 
			"			 			          \r\n" + 
			"			 			          \r\n" + 
			"			 			 from(         \r\n" + 
			"			 			 select t.*,         \r\n" + 
			"			 			 case when cast(REPLACE(ISNULL(t.CREDIT_LIMIT, '0'), ',', '') as NUMERIC(20,2))=0.00          \r\n" + 
			"			 			 then null          \r\n" + 
			"			 			 else  cast((t.overruns/t.CREDIT_LIMIT) as NUMERIC(20,6))         \r\n" + 
			"			 			 end DEVIATION         \r\n" + 
			"			 			  from (         \r\n" + 
			"			 			          \r\n" + 
			"			 			 select DISTINCT          \r\n" + 
			"			 			 a.DOCUMENT_NUMBER,a.PARTNER,rtrim(substring(a.DESCRIPTION,0,(charindex('/',a.DESCRIPTION)))) DESCRIPTION,a.TOTAL_AMT CREDIT_VALUE,a.EXTERNAL_REFER ,a.RISK_CLASS RISK,b.CREDIT_LIMIT,b.MANAGED_BY CC,a.CR_EXPOS EXPOSURE,         \r\n" + 
			"			 			 cast(REPLACE(ISNULL(a.CR_EXPOS, '0'), ',', '') as NUMERIC(20,2))-cast(REPLACE(ISNULL(c.TOTAL_AR, '0'), ',', '') as NUMERIC(20,2)) OPEN_ORDER,         \r\n" + 
			"			 			 cast(REPLACE(ISNULL(a.CR_EXPOS, '0'), ',', '') as NUMERIC(20,2))-cast(REPLACE(ISNULL(b.CREDIT_LIMIT, '0'), ',', '') as NUMERIC(20,2)) OVERRUNS,         \r\n" + 
			"			 			 c.TOTAL_AR RECEIVABLES, REPLACE(c.DAYS_30_M, '- 0', '0') DAYS_30_M  ,d.R_OR_B ,e.SALES ,e.BUINESS_HEAD,e.SALES_HEAD HEAD,e.NAME1 ,f.CUSTOMER_BEHAVIOR_SCORING RATING         \r\n" + 
			"			 			 from BACK_ORDER_43 a         \r\n" + 
			"			 			 left join          \r\n" + 
			"			 			 CREDIT_LIMIT b on a.PARTNER=b.PARTNER         \r\n" + 
			"			 			 LEFT JOIN ZCOP_AGING c on a.PARTNER=c.CUSTOMER         \r\n" + 
			"			 			 LEFT JOIN ORDERING d on a.PARTNER=d.CUSTOMER         \r\n" + 
			"			 			 LEFT JOIN (         \r\n" + 
			"			 			 select t1.PAYER PAYER,t1.SALES SALES,t2.SALES_HEAD SALES_HEAD,t2.BUINESS_HEAD BUINESS_HEAD,t1.NAME1         \r\n" + 
			"			 			 from PY_SALES_MAPPING t1          \r\n" + 
			"			 			 LEFT JOIN SIGMA t2 on t1.SALES=t2.SALES         \r\n" + 
			"			 			 ) e on a.PARTNER=e.PAYER         \r\n" + 
			"			 			 LEFT JOIN SCORING f on a.PARTNER=f.PAYER         \r\n" + 
			"			 			 where R_OR_B ='Release'  and a.payT!='ppd' )t)tt where head ??      \r\n" + 
			"			      \r\n" + 
			"			 )ttt where  UPPER(replace(ORDER_RELEASE_MATRIX,' ',''))= ##   GROUP BY   PARTNER ,       \r\n" + 
			"			 		  DESCRIPTION ,       \r\n" + 
			"			 		  HEAD ,       \r\n" + 
			"			 		  RATING ,       \r\n" + 
			"			 		        \r\n" + 
			"			 		  DAYS_30_M ,       \r\n" + 
			"			 		   RECEIVABLES ,       \r\n" + 
			"			 		  OPEN_ORDER ,       \r\n" + 
			"			 		  CREDIT_LIMIT ,       \r\n" + 
			"			 		  EXPOSURE ,       \r\n" + 
			"			 		  OVERRUNS ,       \r\n" + 
			"			 		  DEVIATION_PERCENT  \r\n" + 
			"	\r\n" + 
			" ";
	
	
	public static String getOrderRelaseSql(String[] value) {
		String values=order_release.replace("??", value[0]).replace("##", value[1]);
		return values;
		
	}
}