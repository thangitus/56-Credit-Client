package com.example.a56_credit.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.a56_credit.R;
import com.example.a56_credit.model.City;
import com.example.a56_credit.model.ListCity;
import com.example.a56_credit.model.PersonalInformation;
import com.example.a56_credit.network.APIDatabase;
import com.example.a56_credit.network.NetworkProviderDatabase;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
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
   private static final String TAG = "AddPersonalInfoActivity";
   EditText edtFullName, edtIdNumber, edtBuildingNumber, edtWards;
   TextView tvChoiceHomeTown, tvChoiceProvince, tvChoiceDistrict;
   TextView tvHomeTown, tvProvince, tvDistrict, tvBirthday;
   ConstraintLayout layoutFillBirthdayInput;
   RadioGroup radioGroupGender;
   RadioButton radioButtonMale, radioButtonFemale;
   ImageButton imgCalendar, buttonBack;
   LoginButton loginButton;
   Button buttonDone;
   Intent intent;
   Calendar myCalendar;
   DatePickerDialog datePickerDialog;

   APIDatabase apiDatabase;
   List<String> tittleCityList, tittleDistrictList;
   PersonalInformation personalInformation;
   CallbackManager callbackManager;
   AccessToken accessToken;
   Boolean isRunningCheckDataEmpty = true;
   Handler handler;
   Boolean isDisable = false;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_add_personal_info);
      mapping();
      showDialog();
      sendDatabaseRequestCity();
      intent = getIntent();
      login();
      Boolean isEdit = intent.getExtras().getBoolean("isEdit");
      if (isEdit) {
         personalInformation = intent.getParcelableExtra("info");
         setData(personalInformation);
      } else {
         personalInformation = new PersonalInformation();
      }
      startThreadCheckDataEmpty();
      buttonBack.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            intent.putExtra("hasData", false);
            setResult(Activity.RESULT_OK, intent);
            finish();
         }
      });

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
            if (checkLogicData(personalInformation)) {
               intent.putExtra("hasData", true);
               intent.putExtra("info", personalInformation);
               setResult(Activity.RESULT_OK, intent);
               finish();
            }
         }
      });

   }

   private void showDialog() {
      LayoutInflater inflater = this.getLayoutInflater();
      View view = inflater.inflate(R.layout.alert_dialog, null);
      AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
      Button button = view.findViewById(R.id.buttonUnderstood);
      dialogBuilder.setView(view);
      AlertDialog alertDialog = dialogBuilder.create();
      alertDialog.show();
      button.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            alertDialog.dismiss();
         }
      });
   }

   private void mapping() {
      edtFullName = findViewById(R.id.editTextFillFullName);
      edtIdNumber = findViewById(R.id.editTextFillIdNumber);
      tvBirthday = findViewById(R.id.textViewFillBirthday);
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
      buttonBack = findViewById(R.id.buttonBack);
      loginButton = findViewById(R.id.buttonLogin);
   }

   private void disableButtonDone() {
      buttonDone.getBackground().setAlpha(150);
      buttonDone.setEnabled(false);

   }

   private void enableButtonDone() {
      buttonDone.getBackground().setAlpha(255);
      buttonDone.setEnabled(true);
   }

   @SuppressLint("HandlerLeak")
   private void startThreadCheckDataEmpty() {
      handler = new Handler() {
         @Override
         public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Boolean status = (Boolean) msg.obj;
            Log.d(TAG, "checkDataEmpty: " + status.toString() + " isDisable  " + isDisable.toString());
            if (status) {
               isDisable = false;
               enableButtonDone();
            } else if (!isDisable) {
               isDisable = true;
               disableButtonDone();
            }
         }
      };
      new Thread(new Runnable() {
         @Override
         public void run() {
            while (isRunningCheckDataEmpty) {
               getData();
               Message msg = handler.obtainMessage(1, checkDataEmpty());
               handler.sendMessage(msg);
               try {
                  Thread.sleep(1000);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         }
      }).start();
   }

   private Boolean checkDataEmpty() {
      if (personalInformation.getFullName().equals(""))
         return false;
      if (personalInformation.getIdNumber().equals(""))
         return false;
      if (personalInformation.getBirthday().equals(""))
         return false;
      if (personalInformation.getGender().equals(""))
         return false;
      if (personalInformation.getHomeTown().equals(""))
         return false;
      if (personalInformation.getBuildingNumber().equals(""))
         return false;
      if (personalInformation.getWards().equals(""))
         return false;
      if (personalInformation.getProvince().equals(""))
         return false;
      if (personalInformation.getDistrict().equals(""))
         return false;
      return true;
   }

   private void setData(PersonalInformation personalInformation) {
      edtFullName.setText(personalInformation.getFullName().toUpperCase());
      edtIdNumber.setText(personalInformation.getIdNumber());
      edtWards.setText(personalInformation.getWards());
      edtBuildingNumber.setText(personalInformation.getBuildingNumber());
      tvBirthday.setText(personalInformation.getBirthday());
      tvHomeTown.setText(personalInformation.getHomeTown());
      tvDistrict.setText(personalInformation.getDistrict());
      tvProvince.setText(personalInformation.getProvince());
      if (personalInformation.getGender().equals("Nam") || personalInformation.getGender().equals("male"))
         radioButtonMale.setChecked(true);
      else radioButtonFemale.setChecked(true);
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
            tvBirthday.setText(sdf.format(myCalendar.getTime()));
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
      if (tittleCityList == null) {
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
      checkDataEmpty();
   }

   private void getData() {
      String idNumber, fullName, birthday, homeTown, buildingNumber, province, district, wards;
      fullName = edtFullName.getText().toString();
      idNumber = edtIdNumber.getText().toString();
      birthday = tvBirthday.getText().toString();
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

   //   private Boolean checkData(PersonalInformation personalInformation) {
//      if (personalInformation.getFullName().equals("")) {
//         Toast.makeText(this, "Bạn chưa nhập họ tên", Toast.LENGTH_SHORT).show();
//         return false;
//      }
//      if (personalInformation.getIdNumber().equals("")) {
//         Toast.makeText(this, "Bạn chưa nhập số CMND", Toast.LENGTH_SHORT).show();
//         return false;
//      } else if (personalInformation.getIdNumber().length() < 9) {
//         Toast.makeText(this, "Số CMND không hợp lệ", Toast.LENGTH_SHORT).show();
//         return false;
//      }
//      if (personalInformation.getBirthday().equals("")) {
//         Toast.makeText(this, "Bạn chưa chọn ngày sinh", Toast.LENGTH_SHORT).show();
//         return false;
//      } else {
//         String[] parts = personalInformation.getBirthday().split("-");
//         Calendar cNow = Calendar.getInstance();
//         int year, month, date;
//         date = Integer.parseInt(parts[0]);
//         month = Integer.parseInt(parts[1]);
//         year = Integer.parseInt(parts[2]);
//         int old = cNow.get(Calendar.YEAR) - year;
//         if (month > cNow.get(Calendar.MONTH) ||
//                 (month == cNow.get(Calendar.MONTH)) && date > cNow.get(Calendar.DATE))
//            old--;
//         if (old < 18) {
//            Toast.makeText(this, "Bạn chưa đủ 18 tuổi", Toast.LENGTH_SHORT).show();
//            return false;
//         }
//      }
//
//      if (personalInformation.getGender() == null) {
//         Toast.makeText(this, "Bạn chưa chọn giới tính", Toast.LENGTH_SHORT).show();
//         return false;
//      }
//      if (personalInformation.getHomeTown().equals("Chưa chọn")) {
//         Toast.makeText(this, "Bạn chưa chọn nguyên quán", Toast.LENGTH_SHORT).show();
//         return false;
//      }
//      if (personalInformation.getBuildingNumber().equals("")) {
//         Toast.makeText(this, "Bạn chưa nhập số nhà & tên đường", Toast.LENGTH_SHORT).show();
//         return false;
//      }
//      if (personalInformation.getWards().equals("")) {
//         Toast.makeText(this, "Bạn chưa nhập phường/xã", Toast.LENGTH_SHORT).show();
//         return false;
//      }
//      if (personalInformation.getProvince().equals("Chưa chọn")) {
//         Toast.makeText(this, "Bạn chưa chọn tỉnh/thành phố", Toast.LENGTH_SHORT).show();
//         return false;
//      }
//      if (personalInformation.getDistrict().equals("Chưa chọn")) {
//         Toast.makeText(this, "Bạn chưa chọn quận/huyện", Toast.LENGTH_SHORT).show();
//         return false;
//      }
//      return true;
//   }
   private Boolean checkLogicData(PersonalInformation personalInformation) {
      if (personalInformation.getIdNumber().length() < 9) {
         Toast.makeText(this, "Số CMND không hợp lệ", Toast.LENGTH_SHORT).show();
         return false;
      }

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
      return true;
   }

   private void login() {
      callbackManager = CallbackManager.Factory.create();
      final List<String> permissionsList = new ArrayList<>();
      permissionsList.add("email");
      permissionsList.add("user_gender");
      permissionsList.add("user_hometown");
      permissionsList.add("user_birthday");
      permissionsList.add("user_location");
      loginButton.setPermissions(permissionsList);
      loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
         @Override
         public void onSuccess(LoginResult loginResult) {
            accessToken = loginResult.getAccessToken();
            Log.e(TAG, accessToken.getToken());
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                       @Override
                       public void onCompleted(JSONObject object, GraphResponse response) {
                          try {
                             String birthday = object.getString("birthday");
                             String inputPattern = "MM/dd/yyyy";
                             String outputPattern = "dd-MM-yyyy";
                             SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                             SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
                             Date date;
                             String str = null;
                             try {
                                date = inputFormat.parse(birthday);
                                str = outputFormat.format(date);
                             } catch (ParseException e) {
                                e.printStackTrace();
                             }
                             personalInformation.setBirthday(str);
                             personalInformation.setFullName(object.getString("name"));
                             personalInformation.setGender(object.getString("gender"));
                             JSONObject jsonObjectHometown = object.getJSONObject("hometown");
                             personalInformation.setHomeTown(jsonObjectHometown.getString("name"));

                             JSONObject jsonObjectProvince = object.getJSONObject("location");
                             String province = jsonObjectProvince.getString("name");
                             province = province.replace("Thành phố", "TP");
                             for (int i = 0; i < tittleCityList.size(); i++)
                                if (tittleCityList.get(i).equals(province)) {
                                   personalInformation.setProvince(province);
                                   sendDatabaseRequestDistrict(i + 1);
                                }
                             setData(personalInformation);
                          } catch (JSONException e) {
                             e.printStackTrace();
                          }
                          Log.e(TAG, "Success");
                       }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "name,address,birthday,hometown,first_name,gender,last_name,name_format,location");
            request.setParameters(parameters);
            request.executeAsync();
         }

         @Override
         public void onCancel() {

         }

         @Override
         public void onError(FacebookException error) {
            Log.wtf("MainActivity: ", "Error");
         }
      });
   }


   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      callbackManager.onActivityResult(requestCode, resultCode, data);
      super.onActivityResult(requestCode, resultCode, data);
   }

   public void disconnectFromFacebook() {
      if (AccessToken.getCurrentAccessToken() == null) {
         return; // already logged out
      }
      new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
              .Callback() {
         @Override
         public void onCompleted(GraphResponse graphResponse) {
            LoginManager.getInstance().logOut();
         }
      }).executeAsync();
   }

   @Override
   protected void onDestroy() {
      disconnectFromFacebook();
      isRunningCheckDataEmpty = false;
      super.onDestroy();
   }
}
