package com.example.a56_credit.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a56_credit.R;
import com.example.a56_credit.model.City;
import com.example.a56_credit.model.ListCity;
import com.example.a56_credit.model.PersonalInformation;
import com.example.a56_credit.network.APIDatabase;
import com.example.a56_credit.network.NetworkProviderDatabase;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPersonalInfoActivity extends AppCompatActivity {
   private static final int REQUEST_CODE = 1;

   EditText edtFullName, edtIdNumber, edtBirthday, edtBuildingNumber, edtWards;
   TextView tvChoiceHomeTown, tvChoiceProvince, tvChoiceDistrict;
   TextView tvHomeTown, tvProvince, tvDistrict;
   ConstraintLayout layoutFillBirthdayInput;
   RadioGroup radioGroupGender;
   RadioButton radioButtonMale, radioButtonFemale;
   ImageButton imgCalendar;
   Button buttonDone;
   Intent intent;
   Calendar myCalendar;
   DatePickerDialog datePickerDialog;

   APIDatabase apiDatabase;
   List<String> tittleCityList, tittleDistrictList;
   PersonalInformation personalInformation;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_personal_info);
      mapping();
      changeBackground();
      sendDatabaseRequestCity();
      intent = getIntent();
      Boolean isEdit = intent.getExtras().getBoolean("isEdit");
      if (isEdit) {
         personalInformation = intent.getParcelableExtra("info");
         setData(personalInformation);
      }
      personalInformation = new PersonalInformation();
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
      tvChoiceProvince.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            pickProvince();
         }
      });
      tvChoiceDistrict.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            pickDistrict();
         }
      });
      radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch (i) {
               case R.id.radioButtonMale: {
                  personalInformation.setGender("Nam");
                  break;
               }
               case R.id.radioButtonFemale: {
                  personalInformation.setGender("Nữ");
               }
            }
         }
      });
      buttonDone.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            getData();
            if (checkData(personalInformation)) {
               intent.putExtra("info", personalInformation);
               setResult(Activity.RESULT_OK, intent);
               finish();
            }
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
      radioGroupGender = findViewById(R.id.radioGroupGender);
      buttonDone = findViewById(R.id.buttonDone);
      radioButtonMale = findViewById(R.id.radioButtonMale);
      radioButtonFemale = findViewById(R.id.radioButtonFemale);
   }

   private void setData(PersonalInformation personalInformation) {
      edtFullName.setText(personalInformation.getFullName());
      edtIdNumber.setText(personalInformation.getIdNumber());
      edtWards.setText(personalInformation.getWards());
      edtBuildingNumber.setText(personalInformation.getBuildingNumber());
      edtBirthday.setText(personalInformation.getBirthday());
      tvHomeTown.setText(personalInformation.getHomeTown());
      tvDistrict.setText(personalInformation.getDistrict());
      tvProvince.setText(personalInformation.getProvince());
      if (personalInformation.getGender().equals("Nam"))
         radioButtonMale.setChecked(true);
      else radioButtonFemale.setChecked(true);
   }

   private void changeBackground() {
      edtBirthday.setFocusable(false);
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

   private void sendDatabaseRequestDistrict(int id) {
      String url = "api/city/" + id + "/district";
      Call<List<City>> call = apiDatabase.getListDistrict(url);
      call.enqueue(new Callback<List<City>>() {
         @Override
         public void onResponse(Call<List<City>> call, Response<List<City>> response) {
            List<City> districtList;
            districtList = new ArrayList<>();
            districtList = response.body();
            tittleDistrictList = createTittleDistrictList(districtList);
         }

         @Override
         public void onFailure(Call<List<City>> call, Throwable t) {

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

   private List<String> createTittleDistrictList(List<City> cityList) {
      List<String> tittleDistrictList;
      tittleDistrictList = new ArrayList<>();
      for (int i = 0; i < cityList.size(); i++) {
         String tittle = cityList.get(i).getTittle();
         tittleDistrictList.add(tittle);
      }
      return tittleDistrictList;
   }

   private void pickHomeTown() {
      if (tittleCityList == null) {
         Toast.makeText(this, "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_SHORT).show();
         return;
      }
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

   private void pickProvince() {
      if (tittleDistrictList == null) {
         Toast.makeText(this, "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_SHORT).show();
         return;
      }
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Chọn tỉnh/thành");
      CharSequence[] cs = tittleCityList.toArray(new CharSequence[tittleCityList.size()]);
      builder.setItems(cs, new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialogInterface, int i) {
            tvProvince.setText(tittleCityList.get(i));
            sendDatabaseRequestDistrict(i + 1);
         }
      });
      AlertDialog dialog = builder.create();
      dialog.show();
   }

   private void pickDistrict() {
      if (tittleDistrictList == null) {
         Toast.makeText(this, "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_SHORT).show();
         return;
      }
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Chọn quận/huyện");
      CharSequence[] cs = tittleDistrictList.toArray(new CharSequence[tittleDistrictList.size()]);
      builder.setItems(cs, new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialogInterface, int i) {
            tvDistrict.setText(tittleDistrictList.get(i));
         }
      });
      AlertDialog dialog = builder.create();
      dialog.show();
   }

   private void getData() {
      String idNumber, fullName, birthday, homeTown, buildingNumber, province, district, wards;
      fullName = edtFullName.getText().toString();
      idNumber = edtIdNumber.getText().toString();
      birthday = edtBirthday.getText().toString();
      homeTown = tvHomeTown.getText().toString();
      buildingNumber = edtBuildingNumber.getText().toString();
      wards = edtWards.getText().toString();
      province = tvProvince.getText().toString();
      district = tvDistrict.getText().toString();
      personalInformation.setFullName(fullName);
      personalInformation.setIdNumber(idNumber);
      personalInformation.setBirthday(birthday);
      personalInformation.setHomeTown(homeTown);
      personalInformation.setBuildingNumber(buildingNumber);
      personalInformation.setWards(wards);
      personalInformation.setProvince(province);
      personalInformation.setDistrict(district);
   }

   private Boolean checkData(PersonalInformation personalInformation) {
      if (personalInformation.getFullName().equals("")) {
         Toast.makeText(this, "Bạn chưa nhập họ tên", Toast.LENGTH_SHORT).show();
         return false;
      }
      if (personalInformation.getIdNumber().equals("")) {
         Toast.makeText(this, "Bạn chưa nhập số CMND", Toast.LENGTH_SHORT).show();
         return false;
      } else if (personalInformation.getIdNumber().length() < 9) {
         Toast.makeText(this, "Số CMND không hợp lệ", Toast.LENGTH_SHORT).show();
         return false;
      }
      if (personalInformation.getBirthday().equals("")) {
         Toast.makeText(this, "Bạn chưa chọn ngày sinh", Toast.LENGTH_SHORT).show();
         return false;
      } else {
         String[] parts = personalInformation.getBirthday().split("-");
         Calendar cNow = Calendar.getInstance();
         int year, month, date;
         date = Integer.parseInt(parts[0]);
         month = Integer.parseInt(parts[1]);
         year = Integer.parseInt(parts[2]);
         int old = cNow.get(Calendar.YEAR) - year;
         if (month > cNow.get(Calendar.MONTH) ||
                 (month == cNow.get(Calendar.MONTH)) && date > cNow.get(Calendar.DATE))
            old--;
         if (old < 18) {
            Toast.makeText(this, "Bạn chưa đủ 18 tuổi", Toast.LENGTH_SHORT).show();
            return false;
         }
      }

      if (personalInformation.getGender() == null) {
         Toast.makeText(this, "Bạn chưa chọn giới tính", Toast.LENGTH_SHORT).show();
         return false;
      }
      if (personalInformation.getHomeTown().equals("Chưa chọn")) {
         Toast.makeText(this, "Bạn chưa chọn nguyên quán", Toast.LENGTH_SHORT).show();
         return false;
      }
      if (personalInformation.getBuildingNumber().equals("")) {
         Toast.makeText(this, "Bạn chưa nhập số nhà & tên đường", Toast.LENGTH_SHORT).show();
         return false;
      }
      if (personalInformation.getWards().equals("")) {
         Toast.makeText(this, "Bạn chưa nhập phường/xã", Toast.LENGTH_SHORT).show();
         return false;
      }
      if (personalInformation.getProvince().equals("Chưa chọn")) {
         Toast.makeText(this, "Bạn chưa chọn tỉnh/thành phố", Toast.LENGTH_SHORT).show();
         return false;
      }
      if (personalInformation.getDistrict().equals("Chưa chọn")) {
         Toast.makeText(this, "Bạn chưa chọn quận/huyện", Toast.LENGTH_SHORT).show();
         return false;
      }
      return true;
   }
}
