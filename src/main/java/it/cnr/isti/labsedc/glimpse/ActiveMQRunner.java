/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package it.cnr.isti.labsedc.glimpse;

import java.net.URI;

import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.SslBrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.usage.SystemUsage;

import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;
import it.cnr.isti.labsedc.glimpse.utils.TesterJMSConnection;

public class ActiveMQRunner implements Runnable {

	private static long ACTIVEMQ_MEMORY_USAGE = 0;
	private static long ACTIVEMQ_TEMP_USAGE = 0;
	private String hostWhereToRunInstance;
	private BrokerService broker;
	private TransportConnector connector;
	
	private TesterJMSConnection tester;
	private ActiveMQConnectionFactory connectionFactory;
	private Boolean enableSSL;
	
	public ActiveMQRunner(String hostWhereToRunInstance, long activemqMemoryUsage, long activemqTempUsage, Boolean enableSSL) {
		this.enableSSL = enableSSL;
		this.hostWhereToRunInstance = hostWhereToRunInstance;		
		this.tester = new TesterJMSConnection(this.hostWhereToRunInstance, enableSSL);
		ACTIVEMQ_MEMORY_USAGE = activemqMemoryUsage;
		ACTIVEMQ_TEMP_USAGE = activemqTempUsage;
	}
	
	public synchronized boolean isBrokerStarted() {
		if ((this.broker == null) || (!this.broker.isStarted())) {
			return false;
		}
				
		try {
		    this.tester.testConnection();
		} catch (JMSException e) {
			return false;
		}
		
		return true;
	}

	public void run() {
//		ActiveMQConnectionFactory acmq = new ActiveMQConnectionFactory(hostWhereToRunInstance);
//		acmq.createTopicConnection();
		
		try {
			if (this.enableSSL) { 
				broker = new SslBrokerService();
			} else {
				broker = new BrokerService();
			}
		
			broker.setPersistent(true);
		
			connector = new TransportConnector();
			connector.setUri(new URI(hostWhereToRunInstance));
			broker.addConnector(connector);
			broker.setUseJmx(false);
			
			SystemUsage systemUsage= broker.getSystemUsage();
			systemUsage.getMemoryUsage().setLimit(ACTIVEMQ_MEMORY_USAGE);
			systemUsage.getTempUsage().setLimit(ACTIVEMQ_TEMP_USAGE);		
			broker.start();
			if (enableSSL) {
				connectionFactory = new ActiveMQSslConnectionFactory(hostWhereToRunInstance);	
			} else {
				connectionFactory = new ActiveMQConnectionFactory(hostWhereToRunInstance);	
			}
			connectionFactory.setTrustAllPackages(true);
			connectionFactory.createConnection();

		} catch (Exception e) {
			DebugMessages.error(System.currentTimeMillis(), this.getClass().getSimpleName(),
					this.getClass().getEnclosingMethod().getName());
			
			e.printStackTrace();
		}
	}
}
