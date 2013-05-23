package com.cirarb.cefalog.fragments;

import com.cirarb.cefalog.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

public class AddDialogFragment extends DialogFragment {

    public interface AddDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }
	
	AddDialogListener mListener;
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        try {
        	mListener = (AddDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddDialogListener");
        }
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.fragment_add_dialog, null));
        
        Bundle args = getArguments();
		int title = args == null ? 0 : args.getInt("title", 0);
        if (title != 0)
        	builder.setTitle(title);
        
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   mListener.onDialogPositiveClick(AddDialogFragment.this);
                   }
               })
               .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       mListener.onDialogNegativeClick(AddDialogFragment.this);
                   }
               });

        return builder.create();
    }
  
}
