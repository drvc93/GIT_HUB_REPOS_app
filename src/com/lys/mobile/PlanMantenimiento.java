package com.lys.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import com.lys.mobile.asynctask.ProgramaMantenimientoAsyncTask;
import com.lys.mobile.data.InspeccionData;
import com.lys.mobile.data.ProgramaData;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.Util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Created by User on 14/09/2014.
 */
public class PlanMantenimiento extends Activity {
	ListView listView;
	static final int DATE_DIALOG_IDI = 998;
	static final int DATE_DIALOG_IDF = 999;
	private int year, yearf;
	private int month, monthf;
	private int day, dayf;
	TextView fin;
	TextView ffin;
	TextView periodo;
	TextView maquina;
	String fechainicial = "";
	String fechafinal = "";
	MyApp m;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// blablabla
		super.onCreate(savedInstanceState);
		setContentView(R.layout.planmantenimiento);

		listView = (ListView) findViewById(R.id.listainspeccion); // imageView2
		final Button buscar = (Button) findViewById(R.id.btnbuscarh);

		m = (MyApp) getApplicationContext();

		llenar();

		buscar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

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

					// String.valueOf(formatt.format(d1));
					// String.valueOf(formattf.format(d2));

					if (m.listaplan.size() > 0)
						m.listaplan.clear();

					DataBase basededatos = new DataBase(PlanMantenimiento.this,
							"DBInspeccion", null, 1);
					SQLiteDatabase db = basededatos.getWritableDatabase();

					String query = "SELECT c_periodoinspeccion,d_periocidad,c_descripcion,c_estado from MTP_PERIODOINSPECCION where d_ultimafechamodificacion between '"
							+ String.valueOf(formatt.format(d1))
							+ "' and '"
							+ String.valueOf(formattf.format(d2)) + "'";
					Log.d("sentencia", query);
					Cursor c = db.rawQuery(query, null);
					if (c.moveToFirst()) {

						do {

							ProgramaData ins = new ProgramaData();

							ins.c_periodoinspeccion = c.getString(0);
							ins.d_periocidad = c.getString(1);
							ins.c_descripcion = c.getString(2);
							ins.c_estado = c.getString(3);

							// MyApp m=(MyApp)getApplicationContext();

							m.listaplan.add(ins);

						} while (c.moveToNext());

					} else {

						Toast.makeText(PlanMantenimiento.this, "No hay datoss",
								Toast.LENGTH_SHORT).show();
						Log.d("plan", "vacio");
					}

					c.close();
					db.close();

					listView.setAdapter(new UserItemAdapter(
							getApplicationContext(),
							android.R.layout.simple_list_item_1, m.listaplan));

				} else {

					Toast.makeText(PlanMantenimiento.this,
							"Seleccione una fecha acorde", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
	}

	public class UserItemAdapter extends ArrayAdapter<ProgramaData> {
		private ArrayList<ProgramaData> listcontact;

		public UserItemAdapter(Context context, int textViewResourceId,
				ArrayList<ProgramaData> listcontact) {
			super(context, textViewResourceId, listcontact);
			this.listcontact = listcontact;
		}

		public View getView(final int position, View convertView,
				final ViewGroup parent) {

			View v = convertView;

			final ProgramaData user = listcontact.get(position);

			if (v == null) {

				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				v = vi.inflate(R.layout.con_plan_mantenimiento, null);

			}

			if (user != null) {

				TextView mantse = (TextView) v
						.findViewById(R.id.tmantenimientose);
				TextView mantsm = (TextView) v
						.findViewById(R.id.tmantenimientosm);
				TextView inspeccionse = (TextView) v
						.findViewById(R.id.tinspeccionse);
				TextView inspeccionsm = (TextView) v
						.findViewById(R.id.tinspeccionsm);

				if (mantse != null) {
					mantse.setText(user.c_periodoinspeccion);
				}

				if (mantsm != null) {
					mantsm.setText(user.c_descripcion);
				}

				if (inspeccionse != null) {

					inspeccionse.setText(user.d_periocidad);
				}

				if (inspeccionsm != null) {

					inspeccionsm.setText(user.c_estado);
				}

			}

			return v;

		}

	}

	public void llenar() {

		fin = (TextView) findViewById(R.id.tfih);
		ffin = (TextView) findViewById(R.id.tffh);
		periodo = (TextView) findViewById(R.id.thperiodo);
		maquina = (TextView) findViewById(R.id.thmaquina);

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

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String fi = preferences.getString("fechainp", "");
		String ff = preferences.getString("fechafinp", "");
		String maqui = preferences.getString("maquina", "");
		// String peri= preferences.getString("periocidadp", "");

		String[] items1 = fi.split(" ");
		String[] items2 = ff.split(" ");
		fi = (items1[0]);
		ff = (items2[0]);

		fechainicial = fi;
		fechafinal = ff;

		fin.setText(fi);
		ffin.setText(ff);
		// periodo.setText(peri);
		maquina.setText(maqui);

		listView.setAdapter(new UserItemAdapter(getApplicationContext(),
				android.R.layout.simple_list_item_1, m.listaplan));
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_IDI:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListener, year,
					month - 1, day);
		case DATE_DIALOG_IDF:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListenerf, yearf,
					monthf - 1, dayf);
		}

		return null;
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			// TODO Auto-generated method stub

			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			// if(day<0) day=Integer.parseInt("0"+day);

			// fechainicial=String.valueOf(new
			// StringBuilder().append(year).append("-").append(month+1).append("-").append(day)
			// .append(""));

			fechainicial = String.valueOf(year + "-" + (month + 1) + "-" + day);

			fin.setText(fechainicial);

		}
	};

	private DatePickerDialog.OnDateSetListener datePickerListenerf = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			// TODO Auto-generated method stub
			yearf = selectedYear;
			monthf = selectedMonth;
			dayf = selectedDay;
			// if(dayf<0) day=Integer.parseInt("0"+dayf);

			fechafinal = String
					.valueOf(yearf + "-" + (monthf + 1) + "-" + dayf);
			ffin.setText(fechafinal);

		}
	};

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

}
