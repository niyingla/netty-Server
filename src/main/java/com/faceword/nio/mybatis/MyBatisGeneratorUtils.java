package com.faceword.nio.mybatis;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
/**
 * @Author: zyong
 * @Date: 2018/11/5 11:11
 * @Version 1.0
 */
public class MyBatisGeneratorUtils {

	public static void main(String[] args) throws Exception{

		 List<String> warnings = new ArrayList<String>();
		   boolean overwrite = true;

		   String filePath = MyBatisGeneratorUtils.class.getResource("/mybatis-generator.xml").getPath();
			System.out.println("path:::::"+filePath);
				   File configFile =
				   new File(filePath);

		   ConfigurationParser cp = new ConfigurationParser(warnings);
		   Configuration core = cp.parseConfiguration(configFile) ;
		   DefaultShellCallback callback = new DefaultShellCallback(overwrite) ;
		   MyBatisGenerator myBatisGenerator = new MyBatisGenerator(core, callback, warnings);
		   myBatisGenerator.generate(null);
		   System.out.println("MyBatis Generator success.!..");
	}
}