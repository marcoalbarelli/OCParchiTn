package it.opencontent.android.ocparchitn.db.entities;

import it.opencontent.android.ocparchitn.SOAPMappings.SOAPFotografia;

import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.Context;
import android.util.Log;

public class Struttura {

	private static final String TAG = Struttura.class.getSimpleName();
	public String tipo;

	public boolean sincronizzato = false;
	public boolean hasDirtyData = false;
	public int ultimaSincronizzazione = 0;
	public String erroreRemoto = ""; 

	public String dirtyElements = "0";
	public int rfid = 0;
	public int rfidArea = 0;
	public int idGioco = 0;
	public int numeroFotografie = 0;

	public String gpsx = "0.0";
	public String gpsy = "0.0";

	public String numeroSerie = "0";
	public String descrizioneMarca = "";
	public String descrizioneArea = "";
	public String descrizioneGioco = "";

	public String note = "";
	public boolean hasFoto0 = false;
	public boolean hasFoto1 = false;
	public String foto0 = "";
	public String foto1 = "";
	
	
	public Struttura() {
		this.ultimaSincronizzazione = (int) new Date().getTime()/1000;
	}

	public void addImmagine(Set<Entry<String, Object>> set) {
		Iterator<Entry<String, Object>> i = set.iterator();
		String raw = "";
		int index = 0;
		while (i.hasNext()) {
			Entry<String, Object> e = i.next();
			Log.d(TAG, "Chiave "+e.getKey());
			String rawFotoName = "";
			if(e.getValue() instanceof SOAPFotografia){
				SOAPFotografia f = (SOAPFotografia) e.getValue();
				raw = f.immagine;
				try{
					rawFotoName = f.nomeImmagine;
					String indexes = (String) f.nomeImmagine.subSequence(0, f.nomeImmagine.indexOf("."));
					String[] indexes2 = indexes.split("_");
					try{
						index = Integer.parseInt(indexes2[3]);
					}catch(IndexOutOfBoundsException iobe){
						index =-1;
					}
				} catch (Exception ex){
					ex.printStackTrace();
				}
			} else {
				Log.d(TAG,
						e.getKey()
								+ " "
								+ bindStringToProperty(e.getValue() == null ? "Nullo" : e.getValue(), e.getKey()));
			}

			if (!raw.equals("")) {
				switch (index) {
				case 0:
					foto0 = raw;
					break;
				case 1:
					foto1 = raw;
					break;
				default:
					Log.d(TAG, "Foto con nome non valido "+rawFotoName);
					break;
				}

			}
			
		}

	}

	public Struttura(Set<Entry<String, Object>> set, Context context) {
		this(set, -1, context);
		this.ultimaSincronizzazione = (int) new Date().getTime()/1000;
		
	}


	public Struttura(Set<Entry<String, Object>> set, int rfid_argomento,
			Context context) {
		this.ultimaSincronizzazione = (int) new Date().getTime()/1000;
		Iterator<Entry<String, Object>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Entry<String, Object> e = iterator.next();
//			Log.d(TAG, e.getKey());
			if (e.getKey().equals("descrizioneMarca")) {
				descrizioneMarca = bindStringToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("descrizioneArea")) {
				descrizioneArea = bindStringToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("descrizioneGioco")) {
				descrizioneGioco = bindStringToProperty(e.getValue(),
						e.getKey());
			}
			if (e.getKey().equals("gpsx") && e.getValue() != null) {
				gpsx = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("gpsy") && e.getValue() != null) {
				gpsy = bindStringToProperty(e.getValue(), e.getKey());
			}

			if (e.getKey().equals("note")) {
				note = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("numeroSerie")) {
				numeroSerie = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("rfid")) {
				if(e.getValue() != null){
				rfid = bindIntToProperty(e.getValue(), e.getKey());
				}
			}
			if (e.getKey().equals("rfidArea")) {
				if(e.getValue() != null){
				rfidArea = bindIntToProperty(e.getValue(), e.getKey());
				}
			}
			if (e.getKey().equals("idGioco") && e.getValue() != null) {
				idGioco = bindIntToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("foto0") && e.getValue() != null) {
				foto0 = bindStringToProperty(e.getValue(), e.getKey());
			}
			if (e.getKey().equals("foto1") && e.getValue() != null) {
				foto1 = bindStringToProperty(e.getValue(), e.getKey());
			}


		}

		/*
		 * if (rfid > 0 && context != null) { recoverLocalImages(context); }
		 */

	}

	protected int bindIntToProperty(Object value, String key) {
		int res = 0;
		if (value.getClass().equals(Integer.class)) {
			res = ((Integer) value).intValue();
		} else {

			try {
				res = Integer.parseInt((String) value);

			} catch (Exception e) {
				Log.e(TAG, "integer not parsed");
				if (value.getClass().equals(SoapObject.class)) {
					Log.d(TAG, "Arrivato un valore non gestibile, " + key);
				} else if (value.getClass().equals(SoapPrimitive.class)) {
					SoapPrimitive v = (SoapPrimitive) value;
					Log.d(TAG, v.getAttributeCount()
							+ " sottoelementi da sbobinare per " + key);
					res = Integer.parseInt(v.toString());
				}
			}
		}
		return res;
	}

	protected String bindStringToProperty(Object value, String key) {
		String res = "";
		if (value != null && value.getClass().equals(SoapObject.class)) {
			Log.d(TAG, "Arrivato un valore non gestibile, " + key);
		} else if (value != null
				&& value.getClass().equals(SoapPrimitive.class)) {
			SoapPrimitive v = (SoapPrimitive) value;
			Log.d(TAG, v.getAttributeCount()
					+ " sottoelementi da sbobinare per " + key);
			if (v.getAttributeCount() > 0) {
				res = v.toString();
			} else {
				res = value.toString();
				// return "INDEFINITO";
			}
		} else {
			if(value!=null){
			res = (String) value.toString();
			} 
		}
		return res;
	}

	protected float bindFloatToProperty(Object value, String key) {
		float res = 0;
		if (value.getClass().equals(Integer.class)) {
			res = ((Integer) value).floatValue();
		} else {
			try {
				res = Float.parseFloat((String) value);

			} catch (Exception e) {
				Log.e(TAG, "float not parsed");
				if (value.getClass().equals(SoapObject.class)) {
					Log.d(TAG, "Arrivato un valore non gestibile, " + key);
				} else if (value.getClass().equals(SoapPrimitive.class)) {
					SoapPrimitive v = (SoapPrimitive) value;
					Log.d(TAG, v.getAttributeCount()
							+ " sottoelementi da sbobinare per " + key);
					res = Float.parseFloat(v.toString());
				}
			}
		}
		return res;
	}





}
