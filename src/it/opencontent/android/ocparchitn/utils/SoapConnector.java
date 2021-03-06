package it.opencontent.android.ocparchitn.utils;

import it.opencontent.android.ocparchitn.SOAPMappings.SOAPArea;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPAreaUpdate;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPAutGiochi;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPCodTabella;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPControllo;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPControlloUpdate;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPFotografia;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPFotoupdate;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPGioco;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPGiocoUpdate;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPInfo;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPIntervento;
import it.opencontent.android.ocparchitn.SOAPMappings.SOAPInterventoUpdate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class SoapConnector {

//	private static final String USERNAME = "PG_CED";
//	private static final String PASSWORD = "euforia";
	private static final String TAG = SoapConnector.class.getSimpleName();
	
	private HashMap<String,Object> map =null; 
	private Object result;
	
	public HashMap<String, Object> getMap(){
		return map;
	}
	
	@SuppressWarnings("rawtypes")
	public HashMap<String, Object> soap(String METHOD_NAME, String SOAP_ACTION,
			String NAMESPACE, String URL, PropertyInfo[] properties)
			throws IOException, XmlPullParserException {

		map = new HashMap<String, Object>();
		result = null;
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME); // set up
																		// request
		if (properties != null && properties.length > 0) {
			for (PropertyInfo p : properties) {
				request.addProperty(p);
			}
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11); // put all required data into a soap
										// envelope
		envelope.setOutputSoapObject(request); // prepare request

		
		//new MarshalDouble().register(envelope);
       	new MarshalBase64().register(envelope);   //serialization
       	envelope.encodingStyle = SoapEnvelope.ENC;
       	
        envelope.bodyOut = request;
        envelope.dotNet = true; 
        envelope.setOutputSoapObject(request);
        envelope.setAddAdornments(false);
        envelope.implicitTypes= true;

        envelope.addMapping("http://gioco.parcogiochi/xsd", "Info", SOAPInfo.class);

        envelope.addMapping("http://gioco.parcogiochi/xsd", "Gioco", SOAPGioco.class);
		envelope.addMapping("http://gioco.parcogiochi/xsd", "Area", SOAPArea.class);
		envelope.addMapping("http://gioco.parcogiochi/xsd", "Fotografia", SOAPFotografia.class);
		envelope.addMapping("http://gioco.parcogiochi/xsd", "Controllo", SOAPControllo.class);
		envelope.addMapping("http://gioco.parcogiochi/xsd", "Intervento", SOAPIntervento.class);

		envelope.addMapping("http://gioco.parcogiochi/xsd", "Giocoupdate", SOAPGiocoUpdate.class);
		envelope.addMapping("http://gioco.parcogiochi/xsd", "AreaUpdate", SOAPAreaUpdate.class);
		envelope.addMapping("http://gioco.parcogiochi/xsd", "Fotoupdate", SOAPFotoupdate.class);
		envelope.addMapping("http://gioco.parcogiochi/xsd", "Controlloupdate", SOAPControlloUpdate.class);
		envelope.addMapping("http://gioco.parcogiochi/xsd", "Interventoupdate", SOAPInterventoUpdate.class);

		envelope.addMapping("http://gioco.parcogiochi/xsd", "AutGiochi", SOAPAutGiochi.class);

		envelope.addMapping("http://gioco.parcogiochi/xsd", "CodTabella", SOAPCodTabella.class);

		//Eccezioni
//		envelope.addMapping("http://gioco.parcogiochi/xsd", "SrvGiocoArkAutException", SOAPSrvGiocoArkAutException.class);
//		envelope.addMapping("http://gioco.parcogiochi", "SrvGiocoArkAutException", SOAPSrvGiocoArkAutException.class);
		
		if(AuthCheck.getHeaderOut() != null){
			envelope.headerOut = AuthCheck.getHeaderOut();
		}
		
		HttpTransportSE httpTransport = new HttpTransportSE(URL);

//		StringBuffer auth = new StringBuffer(USERNAME);
//		auth.append(':').append(PASSWORD);
//		byte[] raw = auth.toString().getBytes();
//		auth.setLength(0);
//		auth.append("Basic ");
//		Base64.encode(raw, 0, raw.length, auth);
//		List<HeaderProperty> headers = new ArrayList<HeaderProperty>();
//		headers.add(new HeaderProperty("Authorization", auth.toString())); 

		httpTransport.debug = true; // this is optional, use it if you don't
									// want to use a packet sniffer to check
									// what the sent message was
									// (httpTransport.requestDump)
		
		try {
//			httpTransport.call(SOAP_ACTION, envelope, headers); // send request
			httpTransport.call(SOAP_ACTION, envelope, null); // send request
			result = envelope.getResponse(); // get response
			map.put("headerIn", envelope.headerIn);
			
		} catch(Exception e){
			map.put("string", "Errore remoto generico");
			map.put("dump", httpTransport.responseDump);
			map.put("success", false);
			
			 if(e.getClass().equals(SoapFault.class)){
			  Node detail = (Node) ((SoapFault) e).detail;
			  KvmSerializable exception = Utils.parseExceptionNode(detail);
			  map.put("exception", exception);
			  //map.put("detail", ((SoapFault) e).detail);
			  map.put("string", ((SoapFault) e).faultstring);
			  map.put("success", false);
			  map.put("faultcode", ((SoapFault) e).faultcode);
			  Log.d(TAG,((SoapFault) e).getMessage());
			} 
			
			//result=(SoapObject) envelope.bodyIn;
			Log.e(TAG, "SOAP ERROR:");
			e.printStackTrace();
		} finally {
			Log.d(TAG,httpTransport.requestDump);
			Log.d(TAG,httpTransport.responseDump);			
			
		}
		if (result != null) {
			if(result instanceof SoapPrimitive){
				Log.d(TAG,"Mapped: "+result.getClass().getSimpleName());
				map.put("primitive", result.toString());				
			}else if(result instanceof KvmSerializable){
			Log.d(TAG,"Mapped: "+result.getClass().getSimpleName());
			map.put("mapped", result);	
				
			int props = ((KvmSerializable) result).getPropertyCount();
			Log.d(TAG, " risultano " + props + " proprietà");

				for (int i = 0; i < props; i++) {
	
					PropertyInfo pi = new PropertyInfo();
					((KvmSerializable) result).getPropertyInfo(i, null, pi);
					String key =pi.name;
					
					map.put(key, pi.getValue());
	
				}
			}else if (result instanceof Vector){
				for( int i = 0; i < ((Vector) result).size(); i++){
					String key = ""+i;
					map.put(key, ((Vector) result).get(i));
				}				
			}else{
				
				map.put("success",false);
				map.put("message","Oggetto sconosciuto");
			}
		} else {
			Log.d(TAG, envelope.bodyOut.toString());
		}		
		

 		return map;
	}

}
