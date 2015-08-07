package lds.gw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UnionGwApp {

	private static final Logger LOG = LoggerFactory.getLogger(UnionGwApp.class);

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext(
					new String[] { "applicationContext.xml" });
			context.start();
			
			Thread.currentThread().sleep(5000);
			
			byte[] req = "00000880{\"bankName\":\"CUP_Z\",\"packetsHex\":\"2E02303330393030303130303030202020303030313030303020202030303030303030303030303030303030303030343230F23844818CE080100000004000000041313936303133383231363030363036343531383036303030303030303030303030303030303031303830373134313230313432383930313134303935383038303737333333303231303030383438333631303030303834383336303030303432383839383134303935383636313430323931303037343736383336313030303733333330303039B1B1BEA9B6BAC8A4C9E3D3B0B7FECEF1202020202020202020202020202020202020202020202020313536303233303030303036303030333030303030303030303030303130323030343238383938303830373134303935383030303438333631303030303030343833363030303030333261363362336164313564363834346237623334613232633133353337333033303938363942443537\",\"type\":\"0\",\"isBack\":false,\"isNio\":false,\"isInitiative\":false,\"isXml\":false,\"uuid\":\"a63b3ad15d6844b7b34a22c135373030\",\"succFlag\":false}".getBytes();
		
			
			
			
			System.in.read();
		} catch (Exception e) {
			LOG.error(e.getMessage());
			if (context != null) {
				context.close();
			}
		}
		
	}
}