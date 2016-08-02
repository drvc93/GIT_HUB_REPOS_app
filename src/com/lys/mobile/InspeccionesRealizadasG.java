package com.lys.mobile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.lys.mobile.data.InspeccionRealizadasGData;
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

public class InspeccionesRealizadasG extends Activity {
	String tipos = "AM", estados = "AM";
	static final int DATE_DIALOG_IDI = 998;
	static final int DATE_DIALOG_IDF = 999;
	ListView listView;
	Spinner ttipo,testado;
	String fechainicial = "", cb = "%";
	String fechafinal = "";
	TextView fin, ffin;
	private int year, yearf;
	private int month, monthf;   
	private int day, dayf;
	Button buscar;
	public ArrayList<InspeccionRealizadasGData> lista;

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.inspeccionesg_realizadas);
		setContentView(R.layout.mantinspecciong_realizadas);
		
		listView = (ListView) findViewById(R.id.listainspeccionreg);
		ttipo = (Spinner) findViewById(R.id.thtipo);
		testado = (Spinner) findViewById(R.id.testado);
		buscar = (Button) findViewById(R.id.btnbuscarh);
		fin = (TextView) findViewById(R.id.tfih);
		ffin = (TextView) findViewById(R.id.tffh);

		SimpleDateFormat fsd = new SimpleDateFormat("yyyy-MM-dd");
		String dateCurrente = "";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance().getTime());
		calendar.add(Calendar.MONTH,-1);
		dateCurrente=fsd.format(calendar.getTime());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String date = df.format(Calendar.getInstance().getTime());


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

		lista = new ArrayList<InspeccionRealizadasGData>();
		// spinner select y adapta
		List<String> listatipo = new ArrayList<String>();
		listatipo.add("AMBOS");
		listatipo.add("OTRO");
		listatipo.add("MAQUINA");
		
		//spinner estado
		List<String> listaestado = new ArrayList<String>();
		listaestado.add("AMBOS");
		listaestado.add("INGRESADA");
		listaestado.add("ENVIADA");
		
		ArrayAdapter<String> dataAdapterCon = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listatipo);

		dataAdapterCon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ttipo.setAdapter(dataAdapterCon);

		ttipo.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (position > 0) {

					if (parent.getItemAtPosition(position).toString().equals("OTRO")) {

						tipos = "OT";

						// llenar(condiciones);
						Log.d("TIPO", tipos);

					} else if (parent.getItemAtPosition(position).toString().equals("MAQUINA")) {

						tipos = "MQ";
						// llenar(condiciones);
						Log.d("TIPO", tipos);
					}

				} else {

					tipos = "AM";
					// llenar(condiciones);
				}
				// llenarcon(periodos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		
		ArrayAdapter<String> dataAdapterCon2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaestado);

		dataAdapterCon2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		testado.setAdapter(dataAdapterCon2);

		testado.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (position > 0) {

					if (parent.getItemAtPosition(position).toString().equals("INGRESADA")) {

						estados = "I";

						// llenar(condiciones);
						Log.d("ESTADO", estados);

					} else if (parent.getItemAtPosition(position).toString().equals("ENVIADA")) {

						estados = "E";
						// llenar(condiciones);
						Log.d("ESTADO", estados);
					}

				} else {

					estados = "AM";
					// llenar(condiciones);
				}
				// llenarcon(periodos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		
		llenar(estados);

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
				Log.d("posicion", "" + position);
				
				Intent i = new Intent(InspeccionesRealizadasG.this, DetalleEjecutadoRealizadoG.class);
				i.putExtra("correlativog", correlativo);
				i.putExtra("companiag", compa);
				startActivity(i);

			}
		});

		buscar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Log.d("tipo", tco);

				String query = "";

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

					SimpleDateFormat formatt = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
					SimpleDateFormat formattf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");

					// condiciones=i e a

					String fechainicialq = formatt.format(d1);
					String fechafinalq = formattf.format(d2);

					if (lista.size() > 0)
						lista.clear();

					if (tipos.equals("AM")) {

						// Toast.makeText(InspeccionesRealizadas.this,"Seleccione una maquina a consultar",
						// Toast.LENGTH_SHORT).show();

						// select de todas las maquinas condiciones

						if (estados.equals("AM")) {

							query = "SELECT * from MTP_INSPECCIONGENERAL_CAB  where d_fechainspeccion>='"
									+ fechainicialq
									+ "' and d_fechainspeccion <='"
									+ fechafinalq
									+ "' ORDER BY d_fechainspeccion ";// fecha

						} else if (estados.equals("I")) {

							query = "SELECT * from MTP_INSPECCIONGENERAL_CAB WHERE d_fechainspeccion>='"
									+ fechainicialq
									+ "' and d_fechainspeccion <='"
									+ fechafinalq
									+ "' and c_estado='I' ORDER BY d_fechainspeccion ";

						} else if (estados.equals("E")) {

							query = "SELECT * from MTP_INSPECCIONGENERAL_CAB WHERE d_fechainspeccion>='"
									+ fechainicialq
									+ "' and d_fechainspeccion <='"
									+ fechafinalq
									+ "' and  c_estado='E' ORDER BY d_fechainspeccion ";

						}

					} else {

						// select de todas las maquinas condiciones
						if (estados.equals("AM")) {

							query = "SELECT * from MTP_INSPECCIONGENERAL_CAB where d_fechainspeccion>='"
									+ fechainicialq
									+ "' and d_fechainspeccion <='"
									+ fechafinalq
									+ "' and c_tipoinspeccion='"
									+ tipos
									+ "' ORDER BY d_fechainspeccion ";// fecha

						} else if (estados.equals("I")) {

							query = "SELECT * from MTP_INSPECCIONGENERAL_CAB WHERE c_tipoinspeccion>='"
									+ fechainicialq
									+ "' and c_tipoinspeccion <='"
									+ fechafinalq
									+ "' and c_estado='I' and c_tipoinspeccion='"
									+ tipos
									+ "'  ORDER BY c_tipoinspeccion ";

						} else if (estados.equals("E")) {

							query = "SELECT * from MTP_INSPECCIONGENERAL_CAB WHERE c_tipoinspeccion>='"
									+ fechainicialq
									+ "' and c_tipoinspeccion <='"
									+ fechafinalq
									+ "' and c_estado='E' and c_tipoinspeccion='"
									+ tipos
									+ "'  ORDER BY c_tipoinspeccion ";

						}

					}

					DataBase basededatos = new DataBase(InspeccionesRealizadasG.this, "DBInspeccion", null, 1);
					SQLiteDatabase db = basededatos.getWritableDatabase();
					Cursor c = db.rawQuery(query, null);

					if (c.moveToFirst()) {

						do {
							InspeccionRealizadasGData v1 = new InspeccionRealizadasGData();

							v1.c_compania = c.getString(0);
							if (v1.c_compania.equals("00100000")){
								v1.c_compania = "FILTROS LYS";
							}
							v1.n_correlativo = c.getString(1);
							v1.c_tipoinspeccion = c.getString(2);
							v1.c_maquina = c.getString(3);
							v1.c_centrocosto = c.getString(4);
							v1.c_comentario = c.getString(5);
							v1.c_usuarioinspeccion = c.getString(6);
							v1.d_fechainspeccion = c.getString(7);
							v1.c_estado = c.getString(8);
							if (v1.c_estado.equals("E")) {
								v1.c_estado = "ENVIADA";
							} else {
								if (v1.c_estado.equals("I")) {
									v1.c_estado = "INGRESADA";
								}
							}							
							v1.c_usuarioenvio = c.getString(9);
							v1.d_fechaenvio = c.getString(10);
							v1.c_ultimousuario = c.getString(11);
							v1.d_ultimafechamodificacion = c.getString(12);

							lista.add(v1);

						} while (c.moveToNext());

					} else {

						Toast.makeText(InspeccionesRealizadasG.this, "No hay datos ", Toast.LENGTH_SHORT).show();
						Log.d("plan", "vacio");
					}
					c.close();
					db.close();

					listView.setAdapter(new UserItemAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, lista));

				} else {

					Toast.makeText(InspeccionesRealizadasG.this, "Seleccione una fecha acorde", Toast.LENGTH_SHORT).show();
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
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			// TODO Auto-generated method stub
			String dia,mes,anio;
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			// if(day<0) day=Integer.parseInt("0"+day);

			// fechainicial=String.valueOf(new
			// StringBuilder().append(year).append("-").append(month+1).append("-").append(day)
			// .append(""));

			//fechainicial = String.valueOf(year + "-" + (month + 1) + "-" + day);
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
		if (estados.equals("AM")) {
			
			query = "SELECT * from MTP_INSPECCIONGENERAL_CAB WHERE d_fechainspeccion>='"
					+ fechainicialq
					+ "' and d_fechainspeccion <='"
					+ fechafinalq + "' ORDER BY d_fechainspeccion ";

			Log.d("plan", query);
		} else if (estados.equals("I")) {
			
			query = "SELECT * from MTP_INSPECCIONGENERAL_CAB WHERE d_fechainspeccion>='"
					+ fechainicialq
					+ "' and d_fechainspeccion <='"
					+ fechafinalq
					+ "' and c_estado='I' ORDER BY d_fechainspeccion ";
			
			Log.d("plan", "2");

		} else if (estados.equals("E")) {

			query = "SELECT * from MTP_INSPECCIONGENERAL_CAB WHERE d_fechainspeccion>='"
					+ fechainicialq
					+ "' and d_fechainspeccion <='"
					+ fechafinalq
					+ "' and c_estado='E' ORDER BY d_fechainspeccion ";

			Log.d("plan", "3");
		}

		Cursor c = db.rawQuery(query, null);

		if (c.moveToFirst()) {

			do {
				InspeccionRealizadasGData v = new InspeccionRealizadasGData();

				v.c_compania = c.getString(0);
				if (v.c_compania.equals("00100000")){
					v.c_compania = "FILTROS LYS";
				}
				v.n_correlativo = c.getString(1);
				v.c_tipoinspeccion = c.getString(2);
				v.c_maquina = c.getString(3);
				v.c_centrocosto = c.getString(4);
				v.c_comentario = c.getString(5);
				v.c_usuarioinspeccion = c.getString(6);
				v.d_fechainspeccion = c.getString(7);
				v.c_estado = c.getString(8);
				if (v.c_estado.equals("E")) {
					v.c_estado = "ENVIADA";
				} else {
					if (v.c_estado.equals("I")) {
						v.c_estado = "INGRESADA";
					}
				}							
				v.c_usuarioenvio = c.getString(9);
				v.d_fechaenvio = c.getString(10);
				v.c_ultimousuario = c.getString(11);
				v.d_ultimafechamodificacion = c.getString(12);
				
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

	public class UserItemAdapter extends ArrayAdapter<InspeccionRealizadasGData> {
		private ArrayList<InspeccionRealizadasGData> listcontact;

		public UserItemAdapter(Context context, int textViewResourceId, ArrayList<InspeccionRealizadasGData> listcontact) {
			super(context, textViewResourceId, listcontact);
			this.listcontact = listcontact;
		}
		
		public View getView(final int position, View convertView, final ViewGroup parent) {

			View v = convertView;

			final InspeccionRealizadasGData user = listcontact.get(position);

			if (v == null) {

				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				//v = vi.inflate(R.layout.con_realizadasg, null);
				v = vi.inflate(R.layout.det_realizadasg, null);

			}

			if (user != null) {

				//TextView compan = (TextView) v.findViewById(R.id.tc_compania);
				TextView corre = (TextView) v.findViewById(R.id.tn_correlativo);
				TextView tipo = (TextView) v.findViewById(R.id.tc_tipo);
				TextView maqui = (TextView) v.findViewById(R.id.tc_maquina);
				TextView cost = (TextView) v.findViewById(R.id.tc_ccosto);
				TextView comen = (TextView) v.findViewById(R.id.tc_comentario);
				TextView estado = (TextView) v.findViewById(R.id.tc_estado);
				TextView usuin = (TextView) v.findViewById(R.id.tc_usuarioinspg);
				TextView fechain = (TextView) v.findViewById(R.id.td_fechainsp);
				//TextView usult = (TextView) v.findViewById(R.id.tc_usuarioult);
				//TextView fechault = (TextView) v.findViewById(R.id.td_fechault);

				/*if (compan != null) {
					compan.setText(user.c_compania);
				}*/

				if (corre != null) {
					Log.d("corre", user.n_correlativo);
					corre.setText(user.n_correlativo);
				}
				
				if (tipo != null) {
					//tipo.setText(user.c_tipoinspeccion);
					if (user.c_tipoinspeccion.equals("OT")){
						tipo.setText("OTRO");
					} else if (user.c_tipoinspeccion.equals("MQ")) {
						tipo.setText("MAQUINA");
					}
				}

				if (maqui != null) {

					maqui.setText(user.c_maquina);
				}
				
				if (cost != null) {

					cost.setText(user.c_centrocosto);
				}
				
				if (comen != null) {

					comen.setText(user.c_comentario);
				}
				
				if (estado != null) {

					estado.setText(user.c_estado);
				}
				if (usuin != null) {

					usuin.setText(user.c_usuarioinspeccion);
				}
				if (fechain != null) {

					fechain.setText(user.d_fechainspeccion);
				}
				/*if (usult != null) {

					usult.setText(user.c_ultimousuario);
				}
				if (fechault != null) {

					fechault.setText(user.d_ultimafechamodificacion);
				}*/

			}

			return v;

		}

	}

	@Override
	public void onRestart() {
		super.onRestart();

		llenar(estados);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
	
	public void EventoRetroceder(View view) {
		finish();
	}
	
}