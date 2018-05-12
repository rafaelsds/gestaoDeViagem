package com.app.rafael.gestaodeviagem.utilidades;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rafael.gestaodeviagem.R;

/**
 * Created by Rafael on 12/05/2018.
 */

public class AlertDynamic {

    private String title;
    private String message;
    private String positiveBtnText;
    private String negativeBtnText;
    private int icon;
    private Icon visibility;
    private Animation animation;
    private AlertDynamicDialogListener pListener;
    private AlertDynamicDialogListener nListener;
    private int pBtnColor;
    private int nBtnColor;
    private int bgColor;
    private boolean cancel;

    public AlertDynamic(AlertDynamic.Builder builder) {
        this.title = builder.title;
        this.message = builder.message;
        this.icon = builder.icon;
        this.animation = builder.animation;
        this.visibility = builder.visibility;
        this.pListener = builder.pListener;
        this.nListener = builder.nListener;
        this.positiveBtnText = builder.positiveBtnText;
        this.negativeBtnText = builder.negativeBtnText;
        this.pBtnColor = builder.pBtnColor;
        this.nBtnColor = builder.nBtnColor;
        this.bgColor = builder.bgColor;
        this.cancel = builder.cancel;
    }

    public static class Builder {
        private String title;
        private String message;
        private String positiveBtnText;
        private String negativeBtnText;
        private Fragment fragment;
        private int icon;
        private Icon visibility;
        private Animation animation;
        private AlertDynamicDialogListener pListener;
        private AlertDynamicDialogListener nListener;
        private int pBtnColor;
        private int nBtnColor;
        private int bgColor;
        private boolean cancel;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public AlertDynamic.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public AlertDynamic.Builder setBackgroundColor(int bgColor) {
            this.bgColor = bgColor;
            return this;
        }

        public AlertDynamic.Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public AlertDynamic.Builder setPositiveBtnText(String positiveBtnText) {
            this.positiveBtnText = positiveBtnText;
            return this;
        }

        public AlertDynamic.Builder setPositiveBtnBackground(int pBtnColor) {
            this.pBtnColor = pBtnColor;
            return this;
        }

        public AlertDynamic.Builder setNegativeBtnText(String negativeBtnText) {
            this.negativeBtnText = negativeBtnText;
            return this;
        }

        public AlertDynamic.Builder setNegativeBtnBackground(int nBtnColor) {
            this.nBtnColor = nBtnColor;
            return this;
        }

        public AlertDynamic.Builder setIcon(int icon, Icon visibility) {
            this.icon = icon;
            this.visibility = visibility;
            return this;
        }

        public AlertDynamic.Builder setAnimation(Animation animation) {
            this.animation = animation;
            return this;
        }

        public AlertDynamic.Builder OnPositiveClicked(AlertDynamicDialogListener pListener) {
            this.pListener = pListener;
            return this;
        }

        public AlertDynamic.Builder OnNegativeClicked(AlertDynamicDialogListener nListener) {
            this.nListener = nListener;
            return this;
        }

        public AlertDynamic.Builder isCancellable(boolean cancel) {
            this.cancel = cancel;
            return this;
        }

        public AlertDynamic build() {
            final Dialog dialog;
            if(this.animation == Animation.POP) {
                dialog = new Dialog(context, R.style.PopTheme);
            } else if(this.animation == Animation.SIDE) {
                dialog = new Dialog(context, R.style.SideTheme);
            } else if(this.animation == Animation.SLIDE) {
                dialog = new Dialog(context, R.style.SlideTheme);
            } else {
                dialog = new Dialog(context);
            }

            dialog.requestWindowFeature(1);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setCancelable(this.cancel);
            dialog.setContentView(R.layout.alert_dynamic);
            View view = dialog.findViewById(R.id.background);
            TextView title1 = (TextView)dialog.findViewById(R.id.title);
            TextView message1 = (TextView)dialog.findViewById(R.id.message);
            ImageView iconImg = (ImageView)dialog.findViewById(R.id.icon);
            Button nBtn = (Button)dialog.findViewById(R.id.negativeBtn);
            Button pBtn = (Button)dialog.findViewById(R.id.positiveBtn);
            title1.setText(this.title);
            message1.setText(this.message);
            if(this.positiveBtnText != null) {
                pBtn.setText(this.positiveBtnText);
            }

            GradientDrawable bgShape;
            if(this.pBtnColor != 0) {
                bgShape = (GradientDrawable)pBtn.getBackground();
                bgShape.setColor(this.pBtnColor);
            }

            if(this.nBtnColor != 0) {
                bgShape = (GradientDrawable)nBtn.getBackground();
                bgShape.setColor(this.nBtnColor);
            }

            if(this.negativeBtnText != null) {
                nBtn.setText(this.negativeBtnText);
            }

            iconImg.setImageResource(this.icon);
            if(this.visibility == Icon.Visible) {
                iconImg.setVisibility(View.VISIBLE);
            } else {
                iconImg.setVisibility(View.GONE);
            }

            if(this.bgColor != 0) {
                view.setBackgroundColor(this.bgColor);
            }

            if(this.pListener != null) {
                pBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Builder.this.pListener.OnClick();
                        dialog.dismiss();
                    }
                });
            } else {
                pBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }

            if(this.nListener != null) {
                nBtn.setVisibility(View.VISIBLE);
                nBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Builder.this.nListener.OnClick();
                        dialog.dismiss();
                    }
                });
            }

            dialog.show();
            return new AlertDynamic(this);
        }
    }

    public interface AlertDynamicDialogListener {
        void OnClick();
    }

    public enum Icon {
        Visible,
        Gone;

        private Icon() {
        }
    }

    public enum Animation {
        POP,
        SIDE,
        SLIDE;

        private Animation() {
        }
    }
}

