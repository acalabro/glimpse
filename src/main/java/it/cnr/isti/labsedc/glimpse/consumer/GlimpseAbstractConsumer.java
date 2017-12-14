package it.cnr.isti.labsedc.glimpse.consumer;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.xmlbeans.XmlException;

import it.cnr.isti.labsedc.glimpse.xml.complexEventRule.ComplexEventRuleActionListDocument;
import it.cnr.isti.labsedc.glimpse.exceptions.IncorrectRuleFormatException;
import it.cnr.isti.labsedc.glimpse.utils.DebugMessages;
import it.cnr.isti.labsedc.glimpse.utils.Manager;

/**
 *
 * This class represent a generic implementation of the interface
 * {@link GlimpseConsumer}.<br />
 * It provides for extension, the abstract method:
 * {@link #messageReceived(Message)}<br />
 * <br />
 *
 * @author Antonello Calabr&ograve;
 * @version 3.2
 */
public abstract class GlimpseAbstractConsumer implements GlimpseConsumer {

	protected static TopicConnection connection;
	protected static boolean firstMessage = true;
	protected static InitialContext initContext;
	protected Properties settings;

	/**
	 * This method setup the connection
	 *
	 * @param settings,
	 *         the settings properties for the connection to the Monitoring
	 *         infrastructure. The Properties object may also be created using the
	 *         method
	 *         {@link Manager#createConsumerSettingsPropertiesObject(String, String, String, String, String, String, boolean, String)}
	 */
	protected void init(Properties settings) {
		try {
			this.settings = settings;
			initContext = this.initConnection(settings, true);
			connection = this.createConnection(initContext, settings, true);
			TopicSubscriber tSub = this.createSubscriber(connection, initContext, "serviceTopic", true);
			tSub.setMessageListener(this);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * This constructor allow to create a {@link GlimpseAbstractConsumer}
	 * object<br />
	 * providing the {@link #settings} properties and a {@link String} object that
	 * will be<br />
	 * automatically converted to a {@link ComplexEventRuleActionListDocument}
	 * object.<br />
	 *
	 * @param settings
	 *         can be generated automatically using
	 *         {@link Manager#createConsumerSettingsPropertiesObject(String, String, String, String, String, String, boolean, String)}.
	 * @param plainTextRule
	 *         a plain text rule is a String containing the Drools<br />
	 *         (or other cep engine implemented) rule that can be generated
	 *         structured<br />
	 *         using the {@link ComplexEventRuleActionListDocument} classes.<br />
	 *         For a rule example see the exampleRule.xml file.
	 */

	public GlimpseAbstractConsumer(Properties settings, String plainTextRule, String usersInvolvedID, String sessionID,
			String bpmnID) {
		init(settings);
		try {
			this.sendTextMessageWithLearnersList(connection, initContext, "serviceTopic", plainTextRule, usersInvolvedID,
					sessionID, bpmnID, true);
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * This constructor allow to create a {@link GlimpseAbstractConsumer}
	 * object<br />
	 * providing the {@link #settings} properties and a
	 * {@link ComplexEventRuleActionListDocument} object.
	 *
	 * @param settings
	 *         can be generated automatically using
	 *         {@link Manager#createConsumerSettingsPropertiesObject(String, String, String, String, String, String, boolean, String)}.
	 * @param complexEventRuleXML
	 *         a {@link ComplexEventRuleActionListDocument} object<br />
	 *         that contains the set of rule that will be loaded on the CEP
	 *         knowledge base for the evaluation. The
	 *         {@link ComplexEventRuleActionListDocument} object, can be generated
	 *         from a string, using the method
	 */
	public GlimpseAbstractConsumer(Properties settings, ComplexEventRuleActionListDocument complexEventRuleXML) {

		try {
			init(settings);
			this.sendActionListMessage(connection, initContext, "serviceTopic", complexEventRuleXML, true);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public GlimpseAbstractConsumer(Properties settings, String messagePayload, List<String> learnersInvolved,
			String sessionID, String bpmnID) {
		try {
			init(settings);
			this.sendTextMessageWithLearnersListAsArray(connection, initContext, "serviceTopic", messagePayload,
					learnersInvolved, sessionID, bpmnID, true);
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void sendTextMessageWithLearnersListAsArray(TopicConnection connection, InitialContext initContext,
			String serviceTopic, String messagePayload, List<String> learnersInvolved, String sessionID, String bpmnID,
			boolean debug) throws NamingException, JMSException {

		if (debug) {
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Session ");
		}
		TopicSession publishSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Looking up for channel ");
		}
		Topic connectionTopic = (Topic) initContext.lookup(serviceTopic);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Publisher ");
		}
		TopicPublisher tPub = publishSession.createPublisher(connectionTopic);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Message ");
		}
		TextMessage sendMessage = publishSession.createTextMessage();
		sendMessage.setObjectProperty("USERSINVOLVEDID", learnersInvolved);
		sendMessage.setStringProperty("SESSIONID", sessionID);
		sendMessage.setStringProperty("BPMNID", bpmnID);
		sendMessage.setStringProperty("SENDER", settings.getProperty("consumerName"));
		sendMessage.setStringProperty("DESTINATION", "monitor");
		sendMessage.setText(messagePayload);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Publishing message  ");
		}
		tPub.publish(sendMessage);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.line();
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	public void onMessage(Message arg0) {
		try {
				TextMessage msg = (TextMessage) arg0;
				TopicSubscriber newTopic;
				try {
					System.out.println("unused = " + this.getAnswerTopicFromTextMessage(msg));
					newTopic = this.connectToTheResponseChannel(connection, "scoresUpdateResponses", true);
					newTopic.setMessageListener(this);
				} catch (IncorrectRuleFormatException e) {
					System.out.println("IncorrectRuleFromatException raised: INVALID RULE");
				}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method can be used when extending this abstract class to<br />
	 * modify the management of received messages.
	 *
	 * @param arg0
	 *         the received Message
	 * @throws JMSException
	 */
	public abstract void messageReceived(Message arg0) throws JMSException;

	/**
	 * This method setup the connection parameters using the {@link Properties}
	 * object {@link #settings}
	 *
	 * @param settings
	 *         the parameter to setup the connection to the Enterprise Service Bus
	 * @param debug
	 *         the debug value
	 * @return it provides an {@link InitialContext} object that will be used<br />
	 *         during the Consumer <-> Monitoring interaction.
	 * @throws NamingException
	 */
	protected InitialContext initConnection(Properties settings, boolean debug) throws NamingException {
		if (debug)
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(),
					"Creating InitialContext with settings ");
		InitialContext initConn = new InitialContext(settings);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.line();
		}
		return initConn;
	}

	/**
	 * This method setup a {@link TopicConnection} object.
	 *
	 * @param initConn
	 *         the InitialContext object generated using the method
	 *         {@link GlimpseAbstractConsumer#initConnection(Properties, boolean)}.
	 * @param settings
	 *         can be generated automatically using
	 *         {@link GlimpseAbstractConsumer#createSettingsPropertiesObject(String, String, String, String, String, String, boolean, String)};
	 * @param debug
	 * @return a TopicConnection object.
	 * @throws NamingException
	 * @throws JMSException
	 */
	protected TopicConnection createConnection(InitialContext initConn, Properties settings, boolean debug)
			throws NamingException, JMSException {
		if (debug)
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(),
					"Creating ConnectionFactory with settings ");
		TopicConnectionFactory connFact = (TopicConnectionFactory) initConn
				.lookup(settings.getProperty("connectionFactoryNames"));
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating TopicConnection ");
		}
		TopicConnection connection = connFact.createTopicConnection();
		if (debug) {
			DebugMessages.ok();
			DebugMessages.line();
		}
		return connection;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.cnr.isti.labse.glimpse.api.consumer.GlimpseConsumer#sendActionListMessage
	 * (javax.jms.TopicConnection, javax.naming.InitialContext, java.lang.String,
	 * it.cnr.isti.labse.glimpse.xml.complexEventRule.
	 * ComplexEventRuleActionListDocument, boolean)
	 */
	public void sendActionListMessage(TopicConnection connection, InitialContext initContext, String serviceChannel,
			ComplexEventRuleActionListDocument actionList, boolean debug) throws JMSException, NamingException {
		if (debug) {
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Session ");
		}
		TopicSession publishSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Looking up for channel ");
		}
		Topic connectionTopic = (Topic) initContext.lookup(serviceChannel);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Publisher ");
		}
		TopicPublisher tPub = publishSession.createPublisher(connectionTopic);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Message ");
		}
		ObjectMessage sendMessage = publishSession.createObjectMessage();
		sendMessage.setStringProperty("SENDER", settings.getProperty("consumerName"));
		sendMessage.setStringProperty("DESTINATION", "monitor");
		sendMessage.setObject((Serializable) actionList);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Publishing message  ");
		}
		tPub.publish(sendMessage);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.line();
		}
	}

	public void sendTextMessage(TopicConnection connection, InitialContext initContext, String serviceChannel,
			String textToSend, boolean debug) throws JMSException, NamingException {
		if (debug) {
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Session ");
		}
		TopicSession publishSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Looking up for channel ");
		}
		Topic connectionTopic = (Topic) initContext.lookup(serviceChannel);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Publisher ");
		}
		TopicPublisher tPub = publishSession.createPublisher(connectionTopic);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Message ");
		}
		TextMessage sendMessage = publishSession.createTextMessage();
		sendMessage.setStringProperty("SENDER", settings.getProperty("consumerName"));
		sendMessage.setStringProperty("DESTINATION", "monitor");
		sendMessage.setText(textToSend);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Publishing message  ");
		}
		tPub.publish(sendMessage);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.line();
		}
	}

	public void sendTextMessageWithLearnersList(TopicConnection connection, InitialContext initContext,
			String serviceChannel, String textToSend, String usersInvolvedID, String sessionID, String bpmnID, boolean debug)
			throws JMSException, NamingException {
		if (debug) {
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Session ");
		}
		TopicSession publishSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Looking up for channel ");
		}
		Topic connectionTopic = (Topic) initContext.lookup(serviceChannel);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Publisher ");
		}
		TopicPublisher tPub = publishSession.createPublisher(connectionTopic);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Message ");
		}
		TextMessage sendMessage = publishSession.createTextMessage();
		sendMessage.setStringProperty("USERSINVOLVEDID", usersInvolvedID);
		sendMessage.setStringProperty("SESSIONID", sessionID);
		sendMessage.setStringProperty("BPMNID", bpmnID);
		sendMessage.setStringProperty("SENDER", settings.getProperty("consumerName"));
		sendMessage.setStringProperty("DESTINATION", "monitor");
		sendMessage.setText(textToSend);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Publishing message  ");
		}
		tPub.publish(sendMessage);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.line();
		}
	}

	/**
	 * This method creates a subscriber object to send the evaluation request to
	 * the monitoring engine.
	 *
	 * @param connection
	 *         the {@link TopicConnection} created using
	 *         {@link GlimpseAbstractConsumer#createConnection(InitialContext, Properties, boolean)}
	 *         method
	 * @param initContext
	 *         the {@link InitialContext}
	 * @param serviceChannel
	 *         the channel where to send the request
	 * @param debug
	 * @return a {@link TopicSubscriber} object
	 * @throws JMSException
	 * @throws NamingException
	 */
	protected TopicSubscriber createSubscriber(TopicConnection connection, InitialContext initContext,
			String serviceChannel, boolean debug) throws JMSException, NamingException {
		if (debug) {
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Session ");
		}
		TopicSession subscribeSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Connecting to channel ");
		}
		Topic connectionTopic = (Topic) initContext.lookup(serviceChannel);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Create subscriber ");
		}
		TopicSubscriber tSub = subscribeSession.createSubscriber(connectionTopic,
				"DESTINATION = '" + settings.getProperty("consumerName") + "'", true);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Starting connection ");
		}
		connection.start();
		if (debug) {
			DebugMessages.ok();
			DebugMessages.line();
		}
		return tSub;
	}

	/**
	 * This method aid to generate a {@link ComplexEventRuleActionListDocument}
	 * from an XML String
	 *
	 * @param xmlRule
	 *         the XML string containing the rule to send
	 * @param debug
	 * @return a {@link ComplexEventRuleActionListDocument} object
	 * @throws XmlException
	 * @throws JMSException
	 */
	protected ComplexEventRuleActionListDocument createComplexEventRuleActionDocumentFromXMLString(String xmlRule,
			boolean debug) throws JMSException, XmlException {
		if (debug)
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(),
					"Creating ComplexEventRuleActionListDocument with provided XML ");
		ComplexEventRuleActionListDocument theDocument = ComplexEventRuleActionListDocument.Factory.parse(xmlRule);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.line();
		}
		return theDocument;
	}

	/**
	 *
	 * This method connect the Consumer to the response channel where the results
	 * of the<br />
	 * requested evaluation to the monitoring infrastructure will be sent on.
	 *
	 * @param connection
	 *         the connection object currently used
	 * @param responseChannel
	 *         the channel where to connect to.<br />
	 *         The monitoring will provides this parameter after sending the
	 *         evaluation request.
	 * @param debug
	 * @return a {@link TopicSubscriber} object
	 * @throws JMSException
	 */
	protected TopicSubscriber connectToTheResponseChannel(TopicConnection connection, String responseChannel,
			boolean debug) throws JMSException {
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Creating Session ");
		}
		TopicSession subscribeSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Connecting to channel ");
		}
		Topic connectionTopic = subscribeSession.createTopic(responseChannel);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Create subscriber ");
		}
		TopicSubscriber tSub = subscribeSession.createSubscriber(connectionTopic, null, true);
		tSub.setMessageListener(this);
		if (debug) {
			DebugMessages.ok();
			DebugMessages.print(System.currentTimeMillis(), this.getClass().getSimpleName(), "Starting connection ");
		}
		connection.start();
		if (debug) {
			DebugMessages.ok();
			DebugMessages.println(System.currentTimeMillis(), this.getClass().getSimpleName(),
					"Successfully connected to the responseChannel " + responseChannel);
			DebugMessages.line();
		}
		return tSub;
	}

	/**
	 * @param msg
	 *         the first TextMessage received from the monitoring
	 *         infrastructure,<br />
	 *         in response to the submitted evaluation request.
	 * @return a string to be used with the method
	 *         {@link GlimpseAbstractConsumer#connectToTheResponseChannel(TopicConnection, String, boolean)}
	 *         method<br />
	 *         to connect on the response channel
	 * @throws JMSException
	 * @throws IncorrectRuleFormatException
	 */
	protected String getAnswerTopicFromTextMessage(TextMessage msg) throws JMSException, IncorrectRuleFormatException {
		if (msg.getText().substring(0, 14).compareTo("AnswerTopic ==") == 0)
			return msg.getText().substring(15, msg.getText().length());
		else
			throw (new IncorrectRuleFormatException(null));
	}
}
