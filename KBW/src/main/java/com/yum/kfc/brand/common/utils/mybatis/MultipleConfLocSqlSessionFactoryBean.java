package com.yum.kfc.brand.common.utils.mybatis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.atteo.xmlcombiner.XmlCombiner;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author DING Weimin (wei-min.ding@hpe.com) Nov 22, 2017 10:02:50 PM
 *
 */
public class MultipleConfLocSqlSessionFactoryBean extends SqlSessionFactoryBean {

	public void setConfigLocations(Resource[] configLocations) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		documentBuilder.setEntityResolver(new EntityResolver(){//ignore DTD
			@Override
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
			}
		});
		
		XmlCombiner cx = new XmlCombiner(documentBuilder, "name");
		
		for(Resource r: configLocations){
			cx.combine(r.getInputStream());
		}
		
		//cx.buildDocument(baos);
		Document result = cx.buildDocument();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		//<!DOCTYPE configuration PUBLIC "-//www.mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//www.mybatis.org//DTD Config 3.0//EN");
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://mybatis.org/dtd/mybatis-3-config.dtd");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Result output = new StreamResult(baos);
		Source input = new DOMSource(result);
		transformer.transform(input, output);
		
		//FileOutputStream fos = new FileOutputStream("d:\\zxc.xml");
		//fos.write(baos.toByteArray());
		//fos.close();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		
		Resource newResource = new InputStreamResource(bais);
		
		super.setConfigLocation(newResource);
	}

}
