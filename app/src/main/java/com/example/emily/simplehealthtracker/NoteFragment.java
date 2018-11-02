package com.example.emily.simplehealthtracker;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.emily.simplehealthtracker.data.Entry;
import com.example.emily.simplehealthtracker.data.EntryViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoteFragment extends Fragment implements SimpleActivity.XmlClickable {
    @BindView(R.id.et_note) EditText noteEditText;
    @BindView(R.id.btn_save) Button saveButton;
    @BindView(R.id.btn_cancel) Button cancelButton;

    EntryViewModel entryViewModel;
    List<Entry> entryList = new ArrayList<>();
    private final static String LOG_TAG = NoteFragment.class.getSimpleName();

    public NoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        entryViewModel = ViewModelProviders.of(getActivity()).get(EntryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_note, container, false);
        ButterKnife.bind(this, rootView);
        entryViewModel.getEntries().observe(this, new Observer<List<Entry>>() {
            @Override
            public void onChanged(@Nullable List<Entry> entries) {
                entryList = entries;
                int last = entryList.size() - 1;
                Log.d(LOG_TAG, "entered " + entryList.get(last).getDescription());
            }
        });

        return rootView;
    }

    @Override
    public void insertNewRecordOrCancel(View v) {
        noteEditText.setText("");
    }

    @Override
    public void saveChanges(View v) {
        final String noteText = noteEditText.getText().toString();


        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                entryViewModel.addEntry(noteText, 0, 1, System.currentTimeMillis(), getResources().getString(R.string.type_note), 0);
            }
        });

        noteEditText.setText("");
        Toast.makeText(getContext(), getResources().getString(R.string.add_OK), Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            getActivity().onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void checkOff(View v) {

    }

    public void showTimePickerDialog(View v){}
    public void showDatePickerDialog(View v){}

}
