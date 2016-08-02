package com.lys.mobile;

import java.util.List;
import android.app.Activity;
import android.view.*;
import android.widget.*;

public class ModeloRadioButtonadp extends ArrayAdapter<ModeloRadioButton> {
	private final List<ModeloRadioButton> list;
	private final Activity context;
	private ListView ListasObj;

	public ModeloRadioButtonadp(Activity context, List<ModeloRadioButton> list,
			ListView ListObj) {
		super(context, R.layout.modelradiobutton, list);
		this.context = context;
		this.list = list;
		this.ListasObj = ListObj;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.modelradiobutton, parent, false);
		}

		TextView text = (TextView) row.findViewById(R.id.lblName);
		TextView textsub = (TextView) row.findViewById(R.id.lblNameSub);
		CheckedTextView rbcheck = (CheckedTextView) row
				.findViewById(R.id.rbcheck);

		if (this.ListasObj.getCheckedItemPosition() == position)
			rbcheck.setChecked(true);
		else
			rbcheck.setChecked(false);

		ModeloRadioButton EModelo = this.list.get(position);
		text.setText(EModelo.getName());
		textsub.setText(EModelo.getNameSub());

		return row;
	}
}
