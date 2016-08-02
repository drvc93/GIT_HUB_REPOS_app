package com.lys.mobile;

import java.io.File;
import java.util.List;
import com.lys.mobile.util.ModeloCheckBox;
//import com.lys.mobile.util.R;
import android.app.Activity;
import android.graphics.*;
import android.view.*;
import android.widget.*;

public class VerListaImagenadp extends ArrayAdapter<ModeloCheckBox> {
	private final List<ModeloCheckBox> list;
	private final Activity context;

	public VerListaImagenadp(Activity context, List<ModeloCheckBox> list) {
		super(context, R.layout.verlistaimagen_item, list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.verlistaimagen_item, parent, false);
		}

		ImageView imageView = (ImageView) row.findViewById(R.id.imagen);
		TextView lblRuta = (TextView) row.findViewById(R.id.lblRuta);
		TextView lblNombre = (TextView) row.findViewById(R.id.lblNombre);
		CheckedTextView rbcheck = (CheckedTextView) row
				.findViewById(R.id.bxcheck);
		TextView lblPeso = (TextView) row.findViewById(R.id.lblPeso);

		ModeloCheckBox EModelo = this.list.get(position);
		String ruta = EModelo.getName();

		if (EModelo.isSelected())
			rbcheck.setChecked(true);
		else
			rbcheck.setChecked(false);
		if (EModelo.isEnabled())
			rbcheck.setEnabled(true);
		else
			rbcheck.setEnabled(false);

		Bitmap myBitmap = BitmapFactory.decodeFile(ruta);
		imageView.setImageBitmap(myBitmap);
		lblRuta.setText(ruta);

		File file = new File(ruta);
		double peso = ((double) file.length() / (double) 1024);

		int cifras = (int) Math.pow(10, 2);
		peso = Math.rint(peso * cifras) / cifras;

		lblPeso.setText("(" + String.valueOf(peso) + " kb)");
		// lblPeso.setText( String.valueOf(
		// myBitmap.getRowBytes()*myBitmap.getHeight()));

		ruta = ruta.substring(ruta.indexOf("_") + 1, ruta.lastIndexOf("_"));
		lblNombre.setText(ruta);
		return row;
	}

}
