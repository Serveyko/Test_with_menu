package com.example.test_with_menu;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.test_with_menu.ui.plus.PlusFragment;
import com.example.test_with_menu.ui.settings.SettingsFragment;

import java.io.IOException;

public class MyDialogFragment extends AppCompatDialogFragment {

    String message;
    String butt1;
    String butt2;
    String title;
    String method;

    public MyDialogFragment(String _message, String _butt1, String _butt2, String _title, String _method) {
        message = _message;
        butt1 = _butt1;
        butt2 = _butt2;
        title = _title;
        method = _method;
    }


    //Java
// Показан только метод, без предыдущего кода для фрагмента
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton(butt2, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Закрываем окно
                        dialog.cancel();
                    }
                });
        System.out.println();
        if (message.equals("MENU")) {

            return builder.create();
        }
        if ((message.equals("Есть отклонения") || message.equals("Это ваша норма"))) {
            ImageView image = new ImageView(getContext());
            image.setImageResource(message.equals("Есть отклонения") ? R.drawable.sad : R.drawable.smile);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
            image.setLayoutParams(layoutParams);

            image.getLayoutParams().height = 100;
            image.getLayoutParams().width = 100;
            image.requestLayout();
            builder.setView(image);
        } else {
            builder.setPositiveButton(butt1, new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                public void onClick(DialogInterface dialog, int id) {
                    if (method.equals("resetAll"))
                        SettingsFragment.reserAllTrue();
                    else {
                        try {
                            PlusFragment.clearAllTrue();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    dialog.cancel();
                }
            });
        }
        return builder.create();
    }
}
