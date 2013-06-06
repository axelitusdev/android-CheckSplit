package mx.axdev.android.checksplit;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class MainActivity extends Activity {
    // check_total
    protected EditText txt_check_total;
    protected float check_total = 0;
    protected boolean check_total_formatting = false;

    // discount
    protected SeekBar skb_discount;
    protected TextView lbl_discount_symbol;
    protected float discount = 0;       // 0-1 percentage

    // already payed
    protected EditText txt_already_payed;
    protected float already_payed = 0;
    protected boolean already_payed_formatting = false;

    // person_count
    protected EditText txt_person_count;
    protected int person_count = 0;

    // tip
    protected SeekBar skb_tip;
    protected TextView lbl_tip_symbol;
    protected float tip = 0;            // 0-1 percentage

    // apply tip
    protected Spinner dpd_tip;
    protected int dpd_tip_selected = TIP_BEFORE_DISCOUNTS;

    // result
    protected TextView lbl_result;
    protected float sub_total = 0;
    protected float result = 0;

    // round to
    protected Spinner dpd_round_to;
    protected int dpd_round_to_selected = TIP_BEFORE_DISCOUNTS;

    // currency_formatter
    protected DecimalFormat decimal_formatter = null;
    protected DecimalFormat currency_formatter = null;

    // Some constants
    public static final int TIP_BEFORE_DISCOUNTS = 0;
    public static final int TIP_AFTER_DISCOUNTS = 1;

    public static final int ROUND_TO_NONE = 0;
    public static final int ROUND_TO_ONE_UNIT = 1;
    public static final int ROUND_TO_FIVE_UNITS = 2;
    public static final int ROUND_TO_TEN_UNITS = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initialize();
    }

    protected void initialize() {
        // Get activity objects to interact
        this.init_components();

        // Setup the currency currency_formatter
        this.setup_formatters();

        // Setup event listeners
        this.setup_listeners();

        // Run the first calculation
        this.calculate();
    }

    protected void init_components() {
        this.txt_check_total = (EditText) findViewById(R.id.main_txt_check_total);
        this.skb_discount = (SeekBar) findViewById(R.id.main_skb_discount);
        this.lbl_discount_symbol = (TextView) findViewById(R.id.main_lbl_discount_symbol);
        this.txt_already_payed = (EditText) findViewById(R.id.main_txt_already_payed);
        this.txt_person_count = (EditText) findViewById(R.id.main_txt_person_count);
        this.skb_tip = (SeekBar) findViewById(R.id.main_skb_tip);
        this.dpd_tip = (Spinner) findViewById(R.id.main_dpd_apply_tip);
        this.lbl_tip_symbol = (TextView) findViewById(R.id.main_lbl_tip_symbol);
        this.lbl_result = (TextView) findViewById(R.id.main_lbl_result);
        this.dpd_round_to = (Spinner) findViewById(R.id.main_dpd_round_to);
    }

    protected void setup_formatters() {
        // Setup decimal formatter
        try {
            DecimalFormatSymbols decimal_format_symbols = new DecimalFormatSymbols();
            decimal_format_symbols.setDecimalSeparator('.');
            decimal_format_symbols.setMonetaryDecimalSeparator('.');
            decimal_format_symbols.setGroupingSeparator(',');

            this.decimal_formatter = new DecimalFormat("#,##0.00", decimal_format_symbols);
        } catch(Exception ex) {
            this.decimal_formatter = (DecimalFormat) NumberFormat.getNumberInstance();
        }

        // Setup currency formatter
        try {
            DecimalFormatSymbols currency_format_symbols = new DecimalFormatSymbols();
            currency_format_symbols.setCurrencySymbol("$ ");
            currency_format_symbols.setInternationalCurrencySymbol("$ ");
            currency_format_symbols.setDecimalSeparator('.');
            currency_format_symbols.setMonetaryDecimalSeparator('.');
            currency_format_symbols.setGroupingSeparator(',');

            this.currency_formatter = new DecimalFormat("$ #,##0.00", currency_format_symbols);
        } catch(Exception ex) {
            this.currency_formatter = (DecimalFormat) NumberFormat.getCurrencyInstance();
        }
    }

    protected void setup_listeners() {
        // Set event listeners for txt_check_total
        this.txt_check_total.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!check_total_formatting) {
                    try {
                        check_total = Float.parseFloat(editable.toString());
                    } catch(Exception ex) {
                        check_total = 0;
                    }
                    calculate();
                }

                check_total_formatting = false;
            }
        });

        this.txt_check_total.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                check_total_formatting = true;
                if(hasFocus) {
                    if(check_total > 0) {
                        txt_check_total.setText(decimal_formatter.format(check_total));
                    } else {
                        txt_check_total.setText("");
                    }
                } else {
                    if(check_total > 0) {
                        txt_check_total.setText(currency_formatter.format(check_total));
                    } else {
                        txt_check_total.setText("");
                    }
                }
            }
        });

        // Set event listeners for skb_discount
        this.skb_discount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                lbl_discount_symbol.setText(Integer.toString(i) + "%");
                discount = i / 100f;
                calculate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Set event listeners for txt_already_payed
        this.txt_already_payed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!already_payed_formatting) {
                    try {
                        already_payed = Float.parseFloat(editable.toString());
                    } catch(Exception ex) {
                        already_payed = 0;
                    }
                    calculate();
                }

                already_payed_formatting = false;
            }
        });

        this.txt_already_payed.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                already_payed_formatting = true;
                if(hasFocus) {
                    if(already_payed > 0) {
                        txt_already_payed.setText(decimal_formatter.format(already_payed));
                    } else {
                        txt_already_payed.setText("");
                    }
                } else {
                    if(check_total > 0) {
                        txt_already_payed.setText(currency_formatter.format(already_payed));
                    } else {
                        txt_already_payed.setText("");
                    }
                }
            }
        });

        // Set event listeners for txt_person_count
        this.txt_person_count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    person_count = Integer.parseInt(editable.toString());
                } catch(Exception ex) {
                    person_count = 0;
                }
                calculate();
            }
        });

        // Set event listeners for skb_tip
        this.skb_tip.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                lbl_tip_symbol.setText(Integer.toString(i) + "%");
                tip = i / 100f;
                calculate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Set event listeners for dpd_tip
        this.dpd_tip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                dpd_tip_selected = position;
                calculate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Set event listeners for dpd_round_to
        this.dpd_round_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                dpd_round_to_selected = position;
                calculate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        //return true;
        return false;
    }

    protected void calculate() {
        this.calc_discounts_tips();

        result = 0;
        if(person_count > 0) {
            result = sub_total / person_count;
        }

        this.calc_rounding();

        this.lbl_result.setText(this.monetize(result));
    }

    protected void calc_discounts_tips() {
        float sub_discount;
        float sub_tip = 0;

        sub_discount = check_total * discount;
        sub_total = check_total - sub_discount;
        switch (this.dpd_tip_selected) {
            case TIP_BEFORE_DISCOUNTS:
                sub_tip = check_total * tip;
                break;
            case TIP_AFTER_DISCOUNTS:
                sub_tip = sub_total * tip;
                break;
        }

        sub_total += sub_tip;
        sub_total -= Math.max(already_payed, 0);
    }

    protected void calc_rounding() {
        switch(this.dpd_round_to_selected) {
            case ROUND_TO_NONE:
                // do nothing
                break;
            case ROUND_TO_ONE_UNIT:
                result = (float) Math.ceil(result);
                break;
            case ROUND_TO_FIVE_UNITS:
                result = (float) Math.ceil(result / 5) * 5;
                break;
            case ROUND_TO_TEN_UNITS:
                result = (float) Math.ceil(result / 10) * 10;
                break;
        }
    }

    protected String monetize(float result) {
        if(currency_formatter == null) {
            this.setup_formatters();
        }

        return currency_formatter.format(result);
    }
}
