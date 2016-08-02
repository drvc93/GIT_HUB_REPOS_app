package com.lys.mobile;

import java.util.ArrayList;
import android.content.Context;
import android.view.*;
import android.widget.*;

public class ModeloComboadp extends BaseAdapter {

	private LayoutInflater li;
	private ArrayList<ModeloCombo> Listas = new ArrayList<ModeloCombo>();

	public ModeloComboadp(Context context, ArrayList<ModeloCombo> items) {
		li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (items != null)
			Listas = items;
	}

	public int getCount() {
		return Listas.size();
	}

	public Object getItem(int posicion) {
		return Listas.get(posicion);
	}

	public long getItemId(int posicion) {
		return posicion;
	}

	public View getView(int posicion, View convertView, ViewGroup parent) {
		View v = convertView;
		final TextView lblCodigo, lblNombre;
		final ModeloCombo ELista = Listas.get(posicion);
		if (v == null) {
			v = li.inflate(R.layout.modelcombobox, null);
		}

		lblCodigo = (TextView) v.findViewById(R.id.lblCodigo);
		lblCodigo.setText(ELista.getId());

		lblNombre = (TextView) v.findViewById(R.id.lblNombre);
		lblNombre.setText(ELista.getName());

		return v;
	}

}
