package com.lys.mobile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.lys.mobile.data.InspeccionRealizadasData;
import com.lys.mobile.data.MaquinasData;
import com.lys.mobile.util.DataBase;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class InspeccionesRealizadas extends Activity {
	String condiciones = "NULL";
	static final int DATE_DIALOG_IDI = 998;
	static final int DATE_DIALOG_IDF = 999;
	ListView listView;
	Spinner tmaquina;
	String fechainicial = "", cb = "%";
	String fechafinal = "";
	TextView fin, ffin;
	private int year, yearf;
	private int month, monthf;   
	private int day, dayf;
	Button buscar;
	ArrayList<MaquinasData> listama = new ArrayList<MaquinasData>();
	public ArrayList<InspeccionRealizadasData> lista;

	protected void onCreate(Bundle savedInstanceState) {
		// blablabla
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.inspecciones_realizadas);
		setContentView(R.layout.mantinspecciont_realizadas);

		Spinner tipo = (Spinner) findViewById(R.id.ttipoi);
		listView = (ListView) findViewById(R.id.listainspeccionre);
		tmaquina = (Spinner) findViewById(R.id.thmaquina);
		buscar = (Button) findViewById(R.id.btnbuscarh);
		fin = (TextView) findViewById(R.id.tfih_2);
		ffin = (TextView) findViewById(R.id.tffh_2);

		SimpleDateFormat fsd = new SimpleDateFormat("yyyy-MM-dd");
		String dateCurrente = "";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance().getTime());
		calendar.add(Calendar.MONTH,-1);
		dateCurrente=fsd.format(calendar.getTime());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(Calendar.getInstance().getTime());

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String fi =dateCurrente;
		String ff = date;


		fechainicial = fi;
		fechafinal = ff;

		fin.setText(fi);
		ffin.setText(ff);

		fin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String[] items1 = fechainicial.split("-");
				year = Integer.parseInt(items1[0]);
				month = Integer.parseInt(items1[1]);
				day = Integer.parseInt(items1[2]);

				showDialog(DATE_DIALOG_IDI);
			}
		});
		ffin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String[] items1 = fechafinal.split("-");
				yearf = Integer.parseInt(items1[0]);
				monthf = Integer.parseInt(items1[1]);
				dayf = Integer.parseInt(items1[2]);

				showDialog(DATE_DIALOG_IDF);

			}
		});

		lista = new ArrayList<InspeccionRealizadasData>();
		// spinner select y adapta
		List<String> listacond = new ArrayList<String>();
		listacond.add("AMBOS");
		listacond.add("INGRESADA");
		listacond.add("ENVIADA");

		if (listama.size() > 0)
			listama.clear();
		MaquinasData m1 = new MaquinasData();
		m1.c_maquina = "0";
		m1.c_descripcion = "0";
		m1.c_codigobarras = "0";
		m1.c_estado = "0";
		m1.c_familiainspeccion = "0";
		m1.c_centrocosto = "0";

		listama.add(m1);

		DataBase basededatos = new DataBase(this, "DBInspeccion", null, 1);
		SQLiteDatabase db = basededatos.getWritableDatabase();

		String query = "SELECT c_maquina,c_descripcion,c_codigobarras,c_estado,c_familiainspeccion,c_centrocosto from MTP_MAQUINAS  ";
		Cursor c = db.rawQuery(query, null);
		if (c.moveToFirst()) {

			do {

				MaquinasData m = new MaquinasData();
				m.c_maquina = c.getString(0);
				m.c_descripcion = c.getString(1);
				m.c_codigobarras = c.getString(2);
				m.c_estado = c.getString(3);
				m.c_familiainspeccion = c.getString(4);
				m.c_centrocosto = c.getString(5);

				// tmaquina.setText(cbarra);
				// tcentro.setText(centro);
				listama.add(m);

			} while (c.moveToNext());

		} else {

			Toast.makeText(this, "Maquinas no disponible", Toast.LENGTH_SHORT)
					.show();
		}
		c.close();
		db.close();

		// tmaquina.setAdapter(new MaquinaItemAdapter(getApplicationContext(),
		// android.R.layout.simple_spinner_item,listama));

		MaquinaItemAdapter dataAdapterConn = new MaquinaItemAdapter(this, android.R.layout.simple_spinner_item, listama);

		dataAdapterConn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		tmaquina.setAdapter(dataAdapterConn);
		// tcentro.setText(centr);

		tmaquina.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				cb = listama.get(position).c_maquina;
				// centr= listama.get(position).c_centrocosto;
				if (cb.equals("0"))
					cb = "%";

				Log.d("codigo maquina", cb);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		ArrayAdapter<String> dataAdapterCon = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listacond);

		dataAdapterCon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		tipo.setAdapter(dataAdapterCon);

		tipo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (position > 0) {

					if (parent.getItemAtPosition(position).toString().equals("INGRESADA")) {

						condiciones = "I";

						// llenar(condiciones);
						Log.d("CONDI", condiciones);

					} else if (parent.getItemAtPosition(position).toString().equals("ENVIADA")) {

						condiciones = "E";
						// llenar(condiciones);
						Log.d("CONDI", condiciones);
					}

				} else {

					condiciones = "NULL";
					// llenar(condiciones);
				}
				// llenarcon(periodos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		
		llenar(condiciones);

		// onclick item pasa a otra pantalla
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String correlativo = lista.get(position).n_correlativo;
				String compa = lista.get(position).c_compania;
				
				if (compa.equals("FILTROS LYS")){
					compa = "00100000";
				}
				
				Log.d("corre", "" + correlativo);
				Log.d("compa", "" + compa);

				Intent i = new Intent(InspeccionesRealizadas.this,DetalleEjecutadoRealizado.class);
				i.putExtra("cb", lista.get(position).c_maquina);
				i.putExtra("correlativo", correlativo);
				i.putExtra("compania", compa);
				startActivity(i);

			}
		});

		buscar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Log.d("tipo", tco);

				String 	query = "";

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date d1 = null, d2 = null, dif = null, dff = null;
				try {
					d1 = format.parse(fechainicial);
					d2 = format.parse(fechafinal);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (d2.after(d1) && d1.before(d2)) {

					SimpleDateFormat formatt = new SimpleDateFormat(
							"yyyy-MM-dd 00:00:00");
					SimpleDateFormat formattf = new SimpleDateFormat(
							"yyyy-MM-dd 23:59:59");

					// condiciones=i e a

					String fechainicialq = formatt.format(d1);
					String fechafinalq = formattf.format(d2);

					if (lista.size() > 0)
						lista.clear();

					if (cb.equals("%")) {



						// Toast.makeText(InspeccionesRealizadas.this,"Seleccione una maquina a consultar",
						// Toast.LENGTH_SHORT).show();

						// select de todas las maquinas condiciones

						if (condiciones.equals("NULL")) {

							query = "SELECT * from MTP_INSPECCIONMAQUINA_CAB  where d_ultimafechamodificacion>='"
									+ fechainicialq
									+ "' and d_ultimafechamodificacion <='"
									+ fechafinalq
									+ "' ORDER BY d_ultimafechamodificacion ";// fecha

						} else if (condiciones.equals("I")) {

							query = "SELECT * from MTP_INSPECCIONMAQUINA_CAB WHERE d_ultimafechamodificacion>='"
									+ fechainicialq
									+ "' and d_ultimafechamodificacion <='"
									+ fechafinalq
									+ "' and c_estado='I' ORDER BY d_ultimafechamodificacion ";

						} else if (condiciones.equals("E")) {

							query = "SELECT * from MTP_INSPECCIONMAQUINA_CAB WHERE d_ultimafechamodificacion>='"
									+ fechainicialq
									+ "' and d_ultimafechamodificacion <='"
									+ fechafinalq
									+ "' and  c_estado='E' ORDER BY d_ultimafechamodificacion ";

						}

					} else {

						// select de todas las maquinas condiciones
						if (condiciones.equals("NULL")) {

							query = "SELECT * from MTP_INSPECCIONMAQUINA_CAB where d_ultimafechamodificacion>='"
									+ fechainicialq
									+ "' and d_ultimafechamodificacion <='"
									+ fechafinalq
									+ "' and c_maquina='"
									+ cb
									+ "' ORDER BY d_ultimafechamodificacion ";// fecha

						} else if (condiciones.equals("I")) {

							query = "SELECT * from MTP_INSPECCIONMAQUINA_CAB WHERE d_ultimafechamodificacion>='"
									+ fechainicialq
									+ "' and d_ultimafechamodificacion <='"
									+ fechafinalq
									+ "' and c_estado='I' and c_maquina='"
									+ cb
									+ "'  ORDER BY d_ultimafechamodificacion ";

						} else if (condiciones.equals("E")) {

							query = "SELECT * from MTP_INSPECCIONMAQUINA_CAB WHERE d_ultimafechamodificacion>='"
									+ fechainicialq
									+ "' and d_ultimafechamodificacion <='"
									+ fechafinalq
									+ "' and c_estado='E' and c_maquina='"
									+ cb
									+ "'  ORDER BY d_ultimafechamodificacion ";

						}

					}

					DataBase basededatos = new DataBase(
							InspeccionesRealizadas.this, "DBInspeccion", null,
							1);
					SQLiteDatabase db = basededatos.getWritableDatabase();
					Cursor c = db.rawQuery(query, null);

					if (c.moveToFirst()) {

						do {
							InspeccionRealizadasData v1 = new InspeccionRealizadasData();

							v1.c_compania = c.getString(0);
							if (v1.c_compania.equals("00100000")){
								v1.c_compania = "FILTROS LYS";
							}
							v1.n_correlativo = c.getString(1);
							v1.c_maquina = c.getString(2);
							v1.c_condicionmaquina = c.getString(3);
							if (v1.c_condicionmaquina.equals("A")) {
								v1.c_condicionmaquina = "ABIERTA";
							} else {
								if (v1.c_condicionmaquina.equals("C")) {
									v1.c_condicionmaquina = "CERRADA";
								}
							}
							v1.c_comentario = c.getString(4);
							v1.c_estado = c.getString(5);
							if (v1.c_estado.equals("E")) {
								v1.c_estado = "ENVIADA";
							} else {
								if (v1.c_estado.equals("I")) {
									v1.c_estado = "INGRESADA";
								}
							}
							v1.d_fechaInicioInspeccion = c.getString(6);
							v1.d_fechaFinInspeccion = c.getString(7);
							v1.c_periodoinspeccion = c.getString(8);
							query = "SELECT c_descripcion FROM MTP_PERIODOINSPECCION WHERE c_periodoinspeccion = '" + v1.c_periodoinspeccion + "'";
							Cursor c2 = db.rawQuery(query, null);
							if (c2.moveToFirst()) {
								v1.c_periodoinspeccion = c2.getString(0);
							}
							c2.close();
							v1.c_usuarioInspeccion = c.getString(9);
							v1.c_usuarioenvio = c.getString(10);
							v1.d_fechaenvio = c.getString(11);
							v1.c_ultimousuario = c.getString(12);
							v1.d_ultimafechamodificacion = c.getString(13);

							lista.add(v1);

						} while (c.moveToNext());

					} else {

						Toast.makeText(InspeccionesRealizadas.this, "No hay datos ", Toast.LENGTH_SHORT).show();
						Log.d("plan", "vacio");
					}
					c.close();
					db.close();

					listView.setAdapter(new UserItemAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, lista));

				} else {

					Toast.makeText(InspeccionesRealizadas.this, "Seleccione una fecha acorde", Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_IDI:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListener, year, month - 1, day);
		case DATE_DIALOG_IDF:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListenerf, yearf, monthf - 1, dayf);
		}

		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
			// TODO Auto-generated method stub
			String dia,mes,anio;
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;


			anio = String.valueOf(year);
			month = month + 1;
			if (month >= 0 && month <= 9) {
				//mes = "0" + String.valueOf(month + 1);
				mes = "0" + String.valueOf(month);
			} else {
				//mes = String.valueOf(month + 1);
				mes = String.valueOf(month);
			}
			if (day >= 0 && day <= 9) {
				dia = "0" + String.valueOf(day);
			} else {
				dia = String.valueOf(day);
			}
			fechainicial = anio + "-" + mes + "-" + dia;
			
			fin.setText(fechainicial);
			
		}
	};

	private DatePickerDialog.OnDateSetListener datePickerListenerf = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			// TODO Auto-generated method stub
			String diaf,mesf,aniof;
			yearf = selectedYear;
			monthf = selectedMonth;
			dayf = selectedDay;
			// if(dayf<0) day=Integer.parseInt("0"+dayf);
			
			//fechafinal = String.valueOf(yearf + "-" + (monthf + 1) + "-" + dayf);
			aniof = String.valueOf(yearf);
			monthf = monthf + 1;
			if (monthf >= 0 && monthf <= 9) {
				//mesf = "0" + String.valueOf(monthf + 1);
				mesf = "0" + String.valueOf(monthf);
			} else {
				//mesf = String.valueOf(monthf + 1);
				mesf = String.valueOf(monthf);
			}
			if (dayf >= 0 && dayf <= 9) {
				diaf = "0" + String.valueOf(dayf);
			} else {
				diaf = String.valueOf(dayf);
			}
			fechafinal = aniof + "-" + mesf + "-" + diaf;
			
			ffin.setText(fechafinal);
			
		}
	};

	public void llenar(String t) {
		
		// limpiar
		if (lista.size() > 0)
			lista.clear();
		
		SimpleDateFormat formatt = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		SimpleDateFormat formattf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		
		// condiciones=i e a
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = null, d2 = null, dif = null, dff = null;
		try {
			d1 = format.parse(fechainicial);
			d2 = format.parse(fechafinal);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String fechainicialq = formatt.format(d1);
		String fechafinalq = formattf.format(d2);
		
		if (lista.size() > 0)
			lista.clear();
		
		DataBase basededatos = new DataBase(this, "DBInspeccion", null, 1);
		SQLiteDatabase db = basededatos.getWritableDatabase();
		
		String query = "";
		if (condiciones.equals("NULL")) {
			
			query = "SELECT * from MTP_INSPECCIONMAQUINA_CAB  where d_ultimafechamodificacion>='"
					+ fechainicialq
					+ "' and d_ultimafechamodificacion <='"
					+ fechafinalq + "' ORDER BY d_ultimafechamodificacion ";

			Log.d("plan", query);
		} else if (condiciones.equals("I")) {
			
			query = "SELECT * from MTP_INSPECCIONMAQUINA_CAB WHERE d_ultimafechamodificacion>='"
					+ fechainicialq
					+ "' and d_ultimafechamodificacion <='"
					+ fechafinalq
					+ "' and c_estado='I' ORDER BY d_ultimafechamodificacion ";
			
			Log.d("plan", "2");

		} else if (condiciones.equals("E")) {

			query = "SELECT * from MTP_INSPECCIONMAQUINA_CAB   WHERE d_ultimafechamodificacion>='"
					+ fechainicialq
					+ "' and d_ultimafechamodificacion <='"
					+ fechafinalq
					+ "' and c_estado='E' ORDER BY d_ultimafechamodificacion ";

			Log.d("plan", "3");
		}

		Cursor c = db.rawQuery(query, null);

		if (c.moveToFirst()) {

			do {
				InspeccionRealizadasData v = new InspeccionRealizadasData();

				v.c_compania = c.getString(0);
				if (v.c_compania.equals("00100000")){
					v.c_compania = "FILTROS LYS";
				}
				v.n_correlativo = c.getString(1);
				v.c_maquina = c.getString(2);
				v.c_condicionmaquina = c.getString(3);
				if (v.c_condicionmaquina.equals("A")) {
					v.c_condicionmaquina = "ABIERTA";
				} else {
					if (v.c_condicionmaquina.equals("C")) {
						v.c_condicionmaquina = "CERRADA";
					}
				}
				v.c_comentario = c.getString(4);
				v.c_estado = c.getString(5);
				if (v.c_estado.equals("E")) {
					v.c_estado = "ENVIADA";
				} else {
					if (v.c_estado.equals("I")) {
						v.c_estado = "INGRESADA";
					}
				}
				v.d_fechaInicioInspeccion = c.getString(6);
				v.d_fechaFinInspeccion = c.getString(7);
				v.c_periodoinspeccion = c.getString(8);
				query = "SELECT c_descripcion FROM MTP_PERIODOINSPECCION WHERE c_periodoinspeccion = '" + v.c_periodoinspeccion + "'";
				Cursor c2 = db.rawQuery(query, null);
				if (c2.moveToFirst()) {
					v.c_periodoinspeccion = c2.getString(0);
				}
				c2.close();
				v.c_usuarioInspeccion = c.getString(9);
				v.c_usuarioenvio = c.getString(10);
				v.d_fechaenvio = c.getString(11);
				v.c_ultimousuario = c.getString(12);
				v.d_ultimafechamodificacion = c.getString(13);
				
				lista.add(v);

			} while (c.moveToNext());

		} else {

			Toast.makeText(this, "No hay datos ", Toast.LENGTH_SHORT).show();
			Log.d("plan", "vacio");
		}
		c.close();
		db.close();

		// adaptar
		listView.setAdapter(new UserItemAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, lista));

	}

	public class UserItemAdapter extends ArrayAdapter<InspeccionRealizadasData> {
		private ArrayList<InspeccionRealizadasData> listcontact;

		public UserItemAdapter(Context context, int textViewResourceId, ArrayList<InspeccionRealizadasData> listcontact) {
			super(context, textViewResourceId, listcontact);
			this.listcontact = listcontact;
		}
		

		public View getView(final int position, View convertView, final ViewGroup parent) {

			View v = convertView;

			final InspeccionRealizadasData user = listcontact.get(position);

			if (v == null) {

				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				//v = vi.inflate(R.layout.con_realizadas, null);
				v = vi.inflate(R.layout.det_realizadas, null);

			}

			if (user != null) {

				//TextView compan = (TextView) v.findViewById(R.id.tc_compania);
				TextView corre = (TextView) v.findViewById(R.id.tn_correlativo);
				TextView maqui = (TextView) v.findViewById(R.id.tc_maquina);
				TextView cond = (TextView) v.findViewById(R.id.tc_condicionmaquina);
				TextView comen = (TextView) v.findViewById(R.id.tc_comentario);
				TextView estado = (TextView) v.findViewById(R.id.tc_estado);
				TextView in = (TextView) v.findViewById(R.id.td_fechaInicioInspecciond);
				TextView fin = (TextView) v.findViewById(R.id.td_fechaFinInspecciond);
				TextView per = (TextView) v.findViewById(R.id.tc_periodoinspeccion);
				TextView ins = (TextView) v.findViewById(R.id.tc_usuarioInspecciond);
				//TextView ul = (TextView) v.findViewById(R.id.td_ultimafechamodificacion);

				/*if (compan != null) {
					compan.setText(user.c_compania);
				}*/

				if (corre != null) {
					corre.setText(user.n_correlativo);
				}

				if (maqui != null) {

					maqui.setText(user.c_maquina);
				}

				if (cond != null) {

					cond.setText(user.c_condicionmaquina);
				}
				if (comen != null) {

					comen.setText(user.c_comentario);
				}
				if (estado != null) {

					estado.setText(user.c_estado);
				}
				if (in != null) {

					in.setText(user.d_fechaInicioInspeccion);
				}
				if (fin != null) {

					fin.setText(user.d_fechaFinInspeccion);
				}
				if (per != null) {

					per.setText(user.c_periodoinspeccion);
				}
				if (ins != null) {

					ins.setText(user.c_usuarioInspeccion);
				}
				/*if (ul != null) {

					ul.setText(user.d_ultimafechamodificacion);
				}*/

			}

			return v;

		}

	}

	public class MaquinaItemAdapter extends ArrayAdapter<MaquinasData> {
		private ArrayList<MaquinasData> listcontact;

		public MaquinaItemAdapter(Context context, int textViewResourceId, ArrayList<MaquinasData> listcontact) {
			super(context, textViewResourceId, listcontact);
			this.listcontact = listcontact;
		}

		@Override
		public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
			return getCustomView(position, cnvtView, prnt);
		}

		@Override
		public View getView(int pos, View cnvtView, ViewGroup prnt) {
			return getCustomView(pos, cnvtView, prnt);
		}

		public View getCustomView(final int position, View convertView, final ViewGroup parent) {

			View v = convertView;

			final MaquinasData user = listcontact.get(position);

			if (v == null) {

				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				v = vi.inflate(R.layout.con_maquina_lista, parent, false);

			}

			if (user != null) {

				TextView codi = (TextView) v.findViewById(R.id.tcodigo);
				TextView des = (TextView) v.findViewById(R.id.tdes);

				if (user.c_maquina.equals("0")) {

					if (codi != null) {
						codi.setText("--SELECCIONE--");
					}

					des.setVisibility(View.GONE);

				} else {

					des.setVisibility(View.VISIBLE);

					if (codi != null) {
						codi.setText("" + user.c_maquina);
					}

					if (des != null) {
						des.setText("" + user.c_descripcion);
					}
				}

			}

			return v;

		}

	}
	
	public void EventoAtras(View view) {
		this.finish();
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
		llenar(condiciones);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

}
