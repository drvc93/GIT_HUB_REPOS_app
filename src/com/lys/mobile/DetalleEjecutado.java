package com.lys.mobile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.androidquery.AQuery;
import com.lys.mobile.data.DetalleProgramaEjecutado;
import com.lys.mobile.data.InspeccionData;
import com.lys.mobile.data.ProgramaEjecutadoData;
import com.lys.mobile.util.DataBase;
import com.lys.mobile.util.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class DetalleEjecutado extends Activity {
	public ArrayList<DetalleProgramaEjecutado> lista;
	int posicion;
	AQuery aq;
	MyApp app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// blablabla
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detalle_ejecutado);
		lista = new ArrayList<DetalleProgramaEjecutado>();
		aq = new AQuery(getApplicationContext());// imageView1"
		// recupero valor y select y escribo
		
		app = ((MyApp)getApplicationContext());
		TextView user1 = (TextView) findViewById(R.id.tvinllusuariod);
		TextView condicion = (TextView) findViewById(R.id.tcondid);
		TextView maquina = (TextView) findViewById(R.id.tmaquinad);
		TextView descripcion = (TextView) findViewById(R.id.tdescripciond);
		TextView estado = (TextView) findViewById(R.id.testadocad);
		TextView finicio = (TextView) findViewById(R.id.tfind);
		TextView ffin = (TextView) findViewById(R.id.tffind);
		TextView periodo = (TextView) findViewById(R.id.tperiodod);// tfin tffin
		Button com = (Button) findViewById(R.id.tcomentind);
		Button atras = (Button) findViewById(R.id.sendd);
		ListView listView = (ListView) findViewById(R.id.listainspecciond);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			String correla = extras.getString("correlativo");//
			String compa = extras.getString("compania");//

			if (lista.size() > 0)
				lista.clear();
			DataBase basededatos = new DataBase(DetalleEjecutado.this,
					"DBInspeccion", null, 1);
			SQLiteDatabase db = basededatos.getWritableDatabase();

			String query = "SELECT c.c_compania,c.n_correlativo,c.c_maquina,c.c_desMaquina, c.c_condicionmaquina, c.c_comentario, c.c_estado,c.d_fechaInicioInspeccion, c.d_fechaFinInspeccion,c.c_periodoinspeccion, c.c_desPeriodoInspeccion,c.c_usuarioInspeccion,c.n_personainspeccion,c.c_nombreinspeccion,c.c_generoOT,c.n_numeroOT,d.c_compania,d.n_correlativo,d.n_linea,d.c_inspeccion,d.c_desInpeccion,d.c_tipoinspeccion,d.n_porcentajeminimo,d.n_porcentajemaximo,d.n_porcentajeinspeccion,d.c_estado,d.c_comentario,d.c_rutafoto from MTP_INSPECCIONEJE_CAB c,MTP_INSPECCIONEJE_DET d  where d.n_correlativo=c.n_correlativo and d.c_compania=c.c_compania and c.c_compania='"
					+ compa + "' and c.n_correlativo='" + correla+"'";
			Log.d("query", "" + query);
			Cursor c = db.rawQuery(query, null);
			if (c.moveToFirst()) {

				do {

					DetalleProgramaEjecutado p = new DetalleProgramaEjecutado();

					p.c_companiac = c.getString(0);
					p.n_correlativoc = c.getString(1);
					p.c_maquinac = c.getString(2);
					p.c_desMaquinac = c.getString(3);
					p.c_condicionmaquinac = c.getString(4);
					p.c_comentarioc = c.getString(5);
					p.c_estadoc = c.getString(6);
					p.d_fechaInicioInspeccionc = c.getString(7);
					p.d_fechaFinInspeccionc = c.getString(8);
					p.c_periodoinspeccionc = c.getString(9);
					p.c_desPeriodoInspeccionc = c.getString(10);
					p.c_usuarioInspeccionc = c.getString(11);
					p.n_personainspeccionc = c.getString(12);
					p.c_nombreinspeccionc = c.getString(13);
					p.c_generoOTc = c.getString(14);
					p.n_numeroOTc = c.getString(15);

					p.c_companiad = c.getString(16);
					p.n_correlativod = c.getString(17);
					p.n_linead = c.getString(18);
					p.c_inspecciond = c.getString(19);
					p.c_desInpecciond = c.getString(20);
					p.c_tipoinspecciond = c.getString(21);
					p.n_porcentajeminimod = c.getString(22);
					p.n_porcentajemaximod = c.getString(23);
					p.n_porcentajeinspecciond = c.getString(24);
					p.c_estadod = c.getString(25);
					p.c_comentariod = c.getString(26);
					p.c_rutafotod = c.getString(27);

					lista.add(p);

				} while (c.moveToNext());

				user1.setText("" + lista.get(0).c_nombreinspeccionc);
				condicion.setText("" + lista.get(0).c_condicionmaquinac);
				maquina.setText("" + lista.get(0).c_maquinac);
				descripcion.setText("" + lista.get(0).c_desPeriodoInspeccionc);
				estado.setText("" + lista.get(0).c_estadoc);
				finicio.setText("" + lista.get(0).d_fechaInicioInspeccionc);
				ffin.setText("" + lista.get(0).d_fechaFinInspeccionc);
				periodo.setText("" + lista.get(0).c_desPeriodoInspeccionc);
				com.setText("" + lista.get(0).c_comentarioc);

				com.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						final Dialog alertDialog = new Dialog(
								DetalleEjecutado.this);

						alertDialog.setContentView(R.layout.dialogo_comentario);
						alertDialog.setTitle("Comentario");
						alertDialog.setCancelable(false);
						final EditText text = (EditText) alertDialog
								.findViewById(R.id.ecomentario);
						text.setText(lista.get(0).c_comentarioc);

						Button dialogButton = (Button) alertDialog
								.findViewById(R.id.baceptar);
						// if button is clicked, close the custom dialog
						dialogButton.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {

								alertDialog.cancel();

							}
						});

						Button dialogCancelar = (Button) alertDialog
								.findViewById(R.id.bcancelar);
						// if button is clicked, close the custom dialog
						dialogCancelar
								.setOnClickListener(new OnClickListener() {

									public void onClick(View v) {

										alertDialog.cancel();

									}
								});

						alertDialog.show();

					}
				});

				listView.setAdapter(new UserItemAdapter(
						getApplicationContext(),
						android.R.layout.simple_list_item_1, lista));

			} else {

				Toast.makeText(DetalleEjecutado.this,
						"No hay informacion disponible", Toast.LENGTH_SHORT)
						.show();

				Log.d("informacion", "no hay");

			}
			c.close();
			db.close();

		}

		atras.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DetalleEjecutado.this.finish();
			}
		});

	}

	public class UserItemAdapter extends ArrayAdapter<DetalleProgramaEjecutado> {
		private ArrayList<DetalleProgramaEjecutado> listcontact;

		public UserItemAdapter(Context context, int textViewResourceId,ArrayList<DetalleProgramaEjecutado> listcontact) {
			super(context, textViewResourceId, listcontact);
			this.listcontact = listcontact;
		}

		public View getView(final int position, View convertView, final ViewGroup parent) {

			View v = convertView;

			final DetalleProgramaEjecutado user = listcontact.get(position);

			if (v == null) {

				LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				if (user.n_porcentajeminimod.substring(0, 1).equals("0")
						&& user.n_porcentajemaximod.substring(0, 1).equals("0")) {//user.n_porcentajeminimo
																				  //y
																				  //user.n_porcentajemaximo
																				  //=0
					Log.d("aq", "aqui1");
					Log.d("valorr", user.n_porcentajeminimod.substring(0, 1));
					v = vi.inflate(R.layout.con_detalle_inspeccion_se, null);

				} else {
					Log.d("aq", "aqui2");
					Log.d("valorr", user.n_porcentajeminimod.substring(0, 1));
					v = vi.inflate(R.layout.con_detalleinspeccion_sm, null);

				}

			}

			if (user != null) {

				if (user.n_porcentajeminimod.substring(0, 1).equals("0")
						&& user.n_porcentajemaximod.substring(0, 1).equals("0")) {//user.n_porcentajeminimo
																				  //y
																				  //user.n_porcentajemaximo
																				  //=0
					TextView numero = (TextView) v.findViewById(R.id.tnumerod);
					TextView detalle = (TextView) v.findViewById(R.id.tdetalled);
					TextView systema = (TextView) v.findViewById(R.id.tsystemad);
					TextView minimo = (TextView) v.findViewById(R.id.tminimod);
					TextView maximo = (TextView) v.findViewById(R.id.tmaximod);
					TextView campo = (TextView) v.findViewById(R.id.tcampod);
					final Spinner ok = (Spinner) v.findViewById(R.id.tokd);

					final Button comentario = (Button) v.findViewById(R.id.bcomentariod);
					Button imagen = (Button) v.findViewById(R.id.bimagend);

					minimo.setEnabled(false);
					maximo.setEnabled(false);

					if (numero != null) {
						numero.setText(""+user.n_linead);
						//user.n_linead = (String.valueOf(position + 1));
					}
					if (detalle != null) {
						detalle.setText("" + user.c_desInpecciond);
					}
					if (systema != null) {
						systema.setText("" + user.c_tipoinspecciond);
					}
					if (campo != null) {
						// campo.setText(user.n_porcentajeinspeccion);
					}
					if (minimo != null) {

						String stringmi = user.n_porcentajeminimod;

						String Str1 = new String(stringmi);

						if (Str1.substring(0, 3).equals("100")) {

							user.n_porcentajeminimod = Str1.substring(0, 6);

						} else {

							user.n_porcentajeminimod = Str1.substring(0, 5);

						}

						minimo.setText("" + user.n_porcentajeminimod);
					}
					if (maximo != null) {

						String stringma = user.n_porcentajemaximod;

						String Str2 = new String(stringma);

						if (Str2.substring(0, 3).equals("100")) {

							user.n_porcentajemaximod = Str2.substring(0, 6);
						} else {

							user.n_porcentajemaximod = Str2.substring(0, 5);
							
						}

						maximo.setText("" + user.n_porcentajemaximod);
					}
					
					if (comentario != null) {
						// if(user.c_comentario.equals("") ||
						// !(user.c_comentario==null))
						comentario.setText(user.c_comentariod);
					}
					if (imagen != null) {
						// if(user.c_rutafoto.equals("") ||
						// !(user.c_rutafoto==null))
						// imagen.setText(user.c_rutafoto);
					}
					
					List<String> listacond = new ArrayList<String>();
					listacond.add("" + user.c_estadod);

					ArrayAdapter<String> dataAdapterCon = new ArrayAdapter<String>(DetalleEjecutado.this,android.R.layout.simple_spinner_item, listacond);

					dataAdapterCon.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					ok.setAdapter(dataAdapterCon);

					ok.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							
							// llenarcon(periodos);
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub
							
						}
					});

					comentario.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							final Dialog alertDialog = new Dialog(DetalleEjecutado.this);

							alertDialog.setContentView(R.layout.dialogo_comentario);
							alertDialog.setTitle("Comentario");
							alertDialog.setCancelable(false);
							final EditText text = (EditText) alertDialog.findViewById(R.id.ecomentario);
							text.setText("" + user.c_comentariod);
							text.setEnabled(false);

							Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
							// if button is clicked, close the custom dialog
							dialogButton.setOnClickListener(new OnClickListener() {

										public void onClick(View v) {

											alertDialog.cancel();

										}
									});

							Button dialogCancelar = (Button) alertDialog.findViewById(R.id.bcancelar);
							// if button is clicked, close the custom dialog
							dialogCancelar.setOnClickListener(new OnClickListener() {

										public void onClick(View v) {

											alertDialog.cancel();

										}
									});

							alertDialog.show();
						}
					});
					imagen.setText("" + user.c_rutafotod);
					imagen.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							// abre camaraa
							// sonar
							
							posicion = position;
							
							// motrar kla puta foto

							final Dialog alertDialog = new Dialog(DetalleEjecutado.this);
							alertDialog.setContentView(R.layout.dialogo_foto);
							alertDialog.setTitle("Foto");
							alertDialog.setCancelable(false);
							final ImageView ima = (ImageView) alertDialog.findViewById(R.id.ximage);
							// text.setText(""+user.c_rutafotod);
							Log.e("Ruta 1.0",Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
									"/" + user.c_rutafotod);
							try {

								//aq.id(ima).image(user.c_rutafotod, true, true, 400, 0, null, 0, 0.0f);
								Log.e("Ruta 1.1",Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
										"/" + user.c_rutafotod);
								aq.id(ima).image(Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
										"/" + user.c_rutafotod, true, true, 400, 0, null, 0, 0.0f);

							} catch (Exception e) {
								Log.e("Ruta 1.2",Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
										"/" + user.c_rutafotod);
								ima.setImageDrawable(getResources().getDrawable(R.drawable.sin));
							}

							Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
							// if button is clicked, close the custom dialog
							dialogButton.setOnClickListener(new OnClickListener() {

										public void onClick(View v) {
											
											alertDialog.cancel();
											
										}
									});

							alertDialog.show();

						}
					});

				} else  {

					TextView numero = (TextView) v.findViewById(R.id.tnumerod);
					TextView detalle = (TextView) v.findViewById(R.id.tdetalled);
					TextView systema = (TextView) v.findViewById(R.id.tsystemad);
					TextView minimo = (TextView) v.findViewById(R.id.tminimod);
					TextView maximo = (TextView) v.findViewById(R.id.tmaximod);
					final EditText llenar = (EditText) v.findViewById(R.id.tllenarsmd);
					final TextView ok = (TextView) v.findViewById(R.id.tokid);
					final Button comentario = (Button) v.findViewById(R.id.bcomentariod);
					Button imagen = (Button) v.findViewById(R.id.bimagend);

					minimo.setEnabled(false);
					maximo.setEnabled(false);
					//llenar.setEnabled(false);

					if (numero != null) {
						numero.setText(String.valueOf(position + 1));
						user.n_linead = (String.valueOf(position + 1));
					}
					if (detalle != null) {
						detalle.setText("" + user.c_desInpecciond);
					}
					if (systema != null) {
						systema.setText("" + user.c_tipoinspecciond);
					}
					if (minimo != null) {

						String stringmi = user.n_porcentajeminimod;

						String Str1 = new String(stringmi);

						if (Str1.substring(0, 3).equals("100")) {

							user.n_porcentajeminimod = Str1.substring(0, 6);

						} else {

							user.n_porcentajeminimod = Str1.substring(0, 5);

						}

						minimo.setText("" + user.n_porcentajeminimod);
					}
					if (maximo != null) {

						String stringma = user.n_porcentajemaximod;

						String Str2 = new String(stringma);

						if (Str2.substring(0, 3).equals("100")) {

							user.n_porcentajemaximod = Str2.substring(0, 6);
						} else {
							
							user.n_porcentajemaximod = Str2.substring(0, 5);
							
						}

						maximo.setText("" + user.n_porcentajemaximod);
					}
					if (llenar != null) {

						String stringmi = user.n_porcentajeinspecciond;

						String Str1 = new String(stringmi);

						if (Str1.substring(0, 3).equals("100")) {

							user.n_porcentajeinspecciond = Str1.substring(0, 6);
						} else {

							user.n_porcentajeinspecciond = Str1.substring(0, 5);

						}

						llenar.setText("" + user.n_porcentajeinspecciond);
					}

					if (ok != null) {
						// if(user.c_estado.equals("") ||
						// !(user.c_estado==null))
						// ok.setText(user.c_estado);
						if (ok.equals("O")){
							ok.setText("OK");
						}else{
							ok.setText("FALLA");
						}
					}
					if (comentario != null) {
						// if(user.c_comentario.equals("") ||
						// !(user.c_comentario==null))
						comentario.setText(user.c_comentariod);
					}
					if (imagen != null) {
						// if(user.c_rutafoto.equals("") ||
						// !(user.c_rutafoto==null))
						// imagen.setText(user.c_rutafoto);
					}

					comentario.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							final Dialog alertDialog = new Dialog(DetalleEjecutado.this);
							alertDialog.setContentView(R.layout.dialogo_comentario);
							alertDialog.setTitle("Comentario");
							alertDialog.setCancelable(false);
							final EditText text = (EditText) alertDialog.findViewById(R.id.ecomentario);
							text.setText("" + user.c_comentariod);
							text.setEnabled(false);

							Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
							// if button is clicked, close the custom dialog
							dialogButton.setOnClickListener(new OnClickListener() {

										public void onClick(View v) {

											alertDialog.cancel();

										}
									});

							Button dialogCancelar = (Button) alertDialog.findViewById(R.id.bcancelar);
							// if button is clicked, close the custom dialog
							dialogCancelar.setOnClickListener(new OnClickListener() {

										public void onClick(View v) {

											alertDialog.cancel();

										}
									});

							alertDialog.show();
						}
					});
					imagen.setText("" + user.c_rutafotod);
					imagen.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub

							// abre camara
							// sonar

							final Dialog alertDialog = new Dialog(DetalleEjecutado.this);
							alertDialog.setContentView(R.layout.dialogo_foto);
							alertDialog.setTitle("Foto");
							alertDialog.setCancelable(false);
							final ImageView ima = (ImageView) alertDialog.findViewById(R.id.ximage);
							// text.setText(""+user.c_rutafotod);
							Log.e("Ruta 2.0",Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
									"/" + user.c_rutafotod);
							try {

								//aq.id(ima).image(user.c_rutafotod, true, true,400, 0, null, 0, 0.0f);
								Log.e("Ruta 2.1",Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
										"/" + user.c_rutafotod);
								aq.id(ima).image(Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
										"/" + user.c_rutafotod, true, true, 400, 0, null, 0, 0.0f);
								

							} catch (Exception e) {
								Log.e("Ruta 2.2",Environment.getExternalStorageDirectory() + app.getRutaInspeccion() + 
										"/" + user.c_rutafotod);
								ima.setImageDrawable(getResources()
										.getDrawable(R.drawable.sin));
							}

							Button dialogButton = (Button) alertDialog.findViewById(R.id.baceptar);
							// if button is clicked, close the custom dialog
							dialogButton.setOnClickListener(new OnClickListener() {

										public void onClick(View v) {

											alertDialog.cancel();

										}
									});

							alertDialog.show();
						}
					});

				}
				
			}

			return v;

		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		DetalleEjecutado.this.finish();
	}

}
