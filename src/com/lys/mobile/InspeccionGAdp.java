package com.lys.mobile;

import java.util.ArrayList;
import java.util.List;

import com.lys.mobile.Llenar2.MaquinaItemAdapter;
import com.lys.mobile.data.CentroCostoData;
import com.lys.mobile.data.InspeccionGData;
import com.lys.mobile.data.MaquinasData;
import com.lys.mobile.data.TipoRevisionData;
import com.lys.mobile.util.DataBase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class InspeccionGAdp extends ArrayAdapter<InspeccionGData> {
	private ArrayList<InspeccionGData> items;
	private int layoutResourceId;
	private Context context;
	private ArrayList<TipoRevisionData> listaTiposRev = new ArrayList<TipoRevisionData>();
	//private TipoRevisionData TipoRev;
	//private static final int REQUEST_CAMERA = 1;
	
	public InspeccionGAdp(Context context,int layoutResourceId,ArrayList<InspeccionGData> items){
		super(context,layoutResourceId,items);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}
	
	public int getCount() {
		return items.size();
	}

	public InspeccionGData getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public ArrayList<InspeccionGData> getItems() {
		return items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		InspeccionGDataHolder holder = null;
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);
		
		holder = new InspeccionGDataHolder();
		holder.InspeccionG = items.get(position);
		holder.remove = (ImageButton)row.findViewById(R.id.bremove);
		holder.remove.setTag(holder.InspeccionG);
		
		holder.numero = (TextView)row.findViewById(R.id.tnumero);
		setNumeroTextChangeListener(holder);
		
		holder.comentario = (EditText)row.findViewById(R.id.tcomentario);
		setComentarioTextChangeListener(holder);
		
		holder.foto = (Button)row.findViewById(R.id.bimagen);
		if(holder.InspeccionG.getRutaFoto()!=null){
			holder.foto.setText("mi texto");
		}
		
		holder.tiporevision = (Spinner) row.findViewById(R.id.stiporevision);
		if (listaTiposRev.size() > 0)
			listaTiposRev.clear();
		TipoRevisionData tr = new TipoRevisionData();
		tr.setTipoRevisionG("00");
		tr.setDescripcion("00");

		listaTiposRev.add(tr);
		
		DataBase basededatos = new DataBase(context, "DBInspeccion", null, 1);
		SQLiteDatabase db = basededatos.getWritableDatabase();

		String queryc = "SELECT c_tiporevisiong,c_descripcion,c_estado from MTP_TIPOREVISIONG ";
		Cursor cc = db.rawQuery(queryc, null);
		if (cc.moveToFirst()) {

			do {

				TipoRevisionData tr2 = new TipoRevisionData();
				tr2.setTipoRevisionG(cc.getString(0));
				tr2.setDescripcion(cc.getString(1));
				
				listaTiposRev.add(tr2);

			} while (cc.moveToNext());

		} else {
			//listView.setAdapter(null);
			Toast.makeText(context, "Tipos Revisión no disponible.", Toast.LENGTH_SHORT)
					.show();
		}
		cc.close();
		db.close();
		
		TipoRevItemAdapter dataAdapterTr = new TipoRevItemAdapter(context, android.R.layout.simple_spinner_item, listaTiposRev);

		dataAdapterTr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		holder.tiporevision.setAdapter(dataAdapterTr);
		
		holder.tiporevision.setEnabled(false);
		
		setTipoRevsetOnItemSelectedListener(holder);
				
		row.setTag(holder);

		setupItem(holder);
		
		return row;
	}
	
	private void setupItem(InspeccionGDataHolder holder) {
		int pos=-1;
		holder.numero.setText(holder.InspeccionG.getLinea());
		TipoRevisionData tr;
		for (int i = 0; i < holder.tiporevision.getCount(); i++) {
			tr = (TipoRevisionData) holder.tiporevision.getItemAtPosition(i);
			Log.d("i = " + String.valueOf(i), tr.getTipoRevisionG()+"="+holder.InspeccionG.getTipoRevisionG());
			//if (tr.getTipoRevisionG().toString() == holder.InspeccionG.getTipoRevisionG().toString()) {
			if (tr.getTipoRevisionG().toString().compareTo(holder.InspeccionG.getTipoRevisionG().toString())==0) {
				//Log.d("entro" + String.valueOf(i), tr.getTipoRevisionG());
				tr = null;
				pos = i;
			}
		}
		Log.d("TipoRev",holder.InspeccionG.getTipoRevisionG().toString());
		Log.d("pos",String.valueOf(pos));
		holder.tiporevision.setSelection(pos);
		holder.comentario.setText(holder.InspeccionG.getComentario());
		Log.d("comentario",holder.InspeccionG.getComentario());
		holder.foto.setText(holder.InspeccionG.getRutaFoto());
		
	}
	
	public static class InspeccionGDataHolder {
		InspeccionGData InspeccionG;
		TextView numero;
		EditText comentario;
		Button foto;
		ImageButton remove;
		Spinner tiporevision;
	}
	
	private void setNumeroTextChangeListener(final InspeccionGDataHolder holder) {
		holder.numero.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				holder.InspeccionG.setLinea(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void afterTextChanged(Editable s) { }
		});
	}
	
	private void setTipoRevsetOnItemSelectedListener(final InspeccionGDataHolder holder) {
		holder.tiporevision.setOnItemSelectedListener(new OnItemSelectedListener() {
			//int previous = -1;
			@Override
		    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		        //if(previous != position && previous < -1) {
		            // Do something
		        	//parent.getItemAtPosition(position).toString();
		        	//holder.tiporevision.add;
					TipoRevisionData tr;
					tr = (TipoRevisionData) holder.tiporevision.getItemAtPosition(position);
		        	holder.InspeccionG.setTipoRevisionG(tr.getTipoRevisionG());
		        	Log.d("Example", "Item Selected: " + tr.getTipoRevisionG());
		        //}
		        //previous = position;
		    }

		    public void onNothingSelected(AdapterView<?> parent) {}
		});
	}
	
	private void setComentarioTextChangeListener(final InspeccionGDataHolder holder) {
		holder.comentario.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				holder.InspeccionG.setComentario(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void afterTextChanged(Editable s) { }
		});
	}
	private void setFotoTextChangeListener(final InspeccionGDataHolder holder) {
		holder.foto.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				holder.InspeccionG.setRutaFoto(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void afterTextChanged(Editable s) { }
		});
	}
	
	public void setFotoText(int pos,String foto) {
		items.get(pos).setRutaFoto(foto);
		this.notifyDataSetChanged();
	}
	
	/*private void setTipoRevision(int pos,String TipoRev) {
		items.get(pos).setTipoRevisionG(TipoRev);
	}*/
	
	public class TipoRevItemAdapter extends ArrayAdapter<TipoRevisionData> {
		private ArrayList<TipoRevisionData> listcontact;
		
		public TipoRevItemAdapter(Context context, int textViewResourceId, ArrayList<TipoRevisionData> listcontact) {
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

			final TipoRevisionData user = listcontact.get(position);

			if (v == null) {

				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				v = vi.inflate(R.layout.con_tiporev_lista, parent, false);

			}

			if (user != null) {

				TextView codi = (TextView) v.findViewById(R.id.tcodigo);
				TextView des = (TextView) v.findViewById(R.id.tdes);

				if (user.getTipoRevisionG().equals("00")) {

					if (codi != null) {
						codi.setText("--SELECCIONE--");
					}

					des.setVisibility(View.GONE);

				} else {

					des.setVisibility(View.VISIBLE);

					if (codi != null) {
						codi.setText("" + user.getTipoRevisionG());
					}

					if (des != null) {
						des.setText("" + user.getDescripcion());
					}

				}

			}

			return v;

		}
		
	}
}
