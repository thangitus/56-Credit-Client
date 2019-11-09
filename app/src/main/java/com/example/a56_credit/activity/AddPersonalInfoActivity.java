package com.example.a56_credit.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a56_credit.R;
import com.example.a56_credit.model.City;
import com.example.a56_credit.model.ListCity;
import com.example.a56_credit.network.APIDatabase;
import com.example.a56_credit.network.NetworkProviderDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPersonalInfoActivity extends AppCompatActivity {
   EditText edtFullName, edtIdNumber, edtBirthday, edtBuildingNumber, edtWards;
   TextView tvChoiceHomeTown, tvChoiceProvince, tvChoiceDistrict;
   TextView tvHomeTown, tvProvince, tvDistrict;
   ConstraintLayout layoutFillBirthdayInput;
   ImageButton imgCalendar;
   Intent intent;
   Calendar myCalendar = Calendar.getInstance();
   DatePickerDialog datePickerDialog;

   APIDatabase apiDatabase;
   List<String> tittleCityList;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_personal_info);
      mapping();
      changeBackground();
      sendDatabaseRequestCity();
      intent = getIntent();
      imgCalendar.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            datePicker();
         }
      });
      tvChoiceHomeTown.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            pickHomeTown();
         }
      });

   }

   private void mapping() {
      edtFullName = findViewById(R.id.editTextFillFullName);
      edtIdNumber = findViewById(R.id.editTextFillIdNumber);
      edtBirthday = findViewById(R.id.editTextFillBirthday);
      edtBuildingNumber = findViewById(R.id.editTextFillBuildingNumber);
      edtWards = findViewById(R.id.editTextFillWards);
      layoutFillBirthdayInput = findViewById(R.id.linearFillBirthdayInput);
      imgCalendar = findViewById(R.id.imageButtonCalendar);
      tvChoiceHomeTown = findViewById(R.id.textViewChoiceHomeTown);
      tvChoiceProvince = findViewById(R.id.textViewChoiceProvince);
      tvChoiceDistrict = findViewById(R.id.textViewChoiceDistrict);
      tvHomeTown = findViewById(R.id.textViewHomeTown);
      tvProvince = findViewById(R.id.textViewProvince);
      tvDistrict = findViewById(R.id.textViewDistrict);
   }

   private void changeBackground() {
      edtFullName.getBackground().setAlpha(40);
      edtIdNumber.getBackground().setAlpha(40);
      layoutFillBirthdayInput.getBackground().setAlpha(40);
      edtBuildingNumber.getBackground().setAlpha(40);
      edtWards.getBackground().setAlpha(40);
   }

   private void datePicker() {
      myCalendar = Calendar.getInstance();
      int day = myCalendar.get(Calendar.DAY_OF_MONTH);
      int month = myCalendar.get(Calendar.MONTH);
      int year = myCalendar.get(Calendar.YEAR);
      datePickerDialog = new DatePickerDialog(AddPersonalInfoActivity.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
         @Override
         public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            myCalendar.set(Calendar.YEAR, i);
            myCalendar.set(Calendar.MONTH, i1);
            myCalendar.set(Calendar.DAY_OF_MONTH, i2);
            String myFormat = "dd-MM-yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            edtBirthday.setText(sdf.format(myCalendar.getTime()));
         }
      }, year, month, day);
      datePickerDialog.show();
   }

   private void sendDatabaseRequestCity() {
      apiDatabase = NetworkProviderDatabase.getInstance().getRetrofit().create(APIDatabase.class);
      Call<ListCity> call = apiDatabase.getListCity();
      call.enqueue(new Callback<ListCity>() {
         @Override
         public void onResponse(Call<ListCity> call, Response<ListCity> response) {
            ListCity listCity;
            listCity = new ListCity(response.body());
            tittleCityList = createTittleCityList(listCity);
         }

         @Override
         public void onFailure(Call<ListCity> call, Throwable t) {

         }
      });

   }

   private List<String> createTittleCityList(ListCity listCity) {
      List<String> tittleCityList;
      tittleCityList = new ArrayList<>();
      for (int i = 0; i < 63; i++) {
         String tittle = listCity.getCityList().get(i).getTittle();
         tittleCityList.add(tittle);
      }
      return tittleCityList;
   }

   private void pickHomeTown() {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Chọn nguyên quán");
      CharSequence[] cs = tittleCityList.toArray(new CharSequence[tittleCityList.size()]);
      builder.setItems(cs, new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialogInterface, int i) {
            tvHomeTown.setText(tittleCityList.get(i));
         }
      });
      AlertDialog dialog = builder.create();
      dialog.show();

   }
}
