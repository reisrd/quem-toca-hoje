package com.example.quemtocahoje.Views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.quemtocahoje.Enum.StatusProposta;
import com.example.quemtocahoje.Enum.TipoUsuario;
import com.example.quemtocahoje.Model.PropostaDAO;
import com.example.quemtocahoje.Persistencia.Entity.PropostaEntity;
import com.example.quemtocahoje.Utility.DefinirDatas;
import com.example.quemtocahoje.Utility.Mensagem;
import com.example.tcc.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.HOUR;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;

public class TelaProposta extends Activity implements DatePickerDialog.OnDateSetListener {

    private ImageButton imgSelecionarDataProposta;
    private ImageButton imgSelecionarHorarioInicio;
    private ImageButton imgSelecionarHorarioFim;
    private EditText txtDataPropostaEscolhida;
    private EditText edtLocalProposta;
    private EditText edtCacheProposta;
    private EditText edtDescricaoProposta;
    private Button btnEnviarProposta;
    private Button btnVoltarProposta;
    private Button btnRecusarProposta;
    private EditText edtHorarioProposta;
    private EditText edtHorarioFim;
    private TextView lblNomeDestinatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_proposta_enviar);
        edtHorarioProposta = findViewById(R.id.edtHorarioProposta);
        imgSelecionarDataProposta = findViewById(R.id.imgSelecionarDataProposta);
        txtDataPropostaEscolhida = findViewById(R.id.txtDataPropostaEscolhida);
        edtLocalProposta = findViewById(R.id.edtLocalProposta);
        edtCacheProposta = findViewById(R.id.edtCacheProposta);
        edtDescricaoProposta = findViewById(R.id.edtDescricaoProposta);
        btnEnviarProposta = findViewById(R.id.btnEnviarProposta);
        btnVoltarProposta = findViewById(R.id.btnVoltarProposta);
        btnRecusarProposta = findViewById(R.id.btnRecusarProposta);
        edtHorarioFim = findViewById(R.id.edtHorarioFim);
        imgSelecionarHorarioInicio = findViewById(R.id.imgSelecionarHorarioInicio);
        imgSelecionarHorarioFim = findViewById(R.id.imgSelecionarHorarioFim);
        lblNomeDestinatario = findViewById(R.id.lblNomeDestinatario);
        Calendar dataAtual = Calendar.getInstance();
        PropostaDAO dao = new PropostaDAO();

        String intentTela = getIntent().getStringExtra("intentTela");
        if (intentTela.equals("ENVIAR")) {
            lblNomeDestinatario.setText(getIntent().getStringExtra("labelDestinatario"));
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    TelaProposta.this, TelaProposta.this, dataAtual.get(Calendar.YEAR), dataAtual.get(Calendar.MONTH), dataAtual.get(Calendar.DAY_OF_MONTH));


            imgSelecionarDataProposta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                }
            });

            btnEnviarProposta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PropostaEntity p = prepararObjeto();

                    if (validaHorario()) {
                        if (!validaCampos()) {
                            if (!validaCache()) {
                                PropostaDAO dao = new PropostaDAO();
                                dao.enviarNovaProposta(p, TelaProposta.this);

                            } else {
                                Mensagem.notificar(TelaProposta.this, "Aviso", "Valor do cach?? inv??lido");
                            }
                        } else {
                            Mensagem.notificar(TelaProposta.this, "Aviso", "Preencher todos os campos");
                        }
                    //}else {
                       // Mensagem.notificar(TelaProposta.this,"Aviso","Hor??rio inv??lido");
                    }
                }
            });

            //Time Picker que resgata o hor??rio direto no EditText - Hor??rio Inicio
            Calendar c = Calendar.getInstance();
            int hr = c.get(HOUR);
            int min = c.get(MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    edtHorarioProposta.setText(ajustarHoraMinuto(i, i1));
                }
            }, hr, min, true);


            imgSelecionarHorarioInicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    timePickerDialog.show();

                }

            });

            //Time Picker que resgata o hor??rio direto no EditText - Hor??rio Fim
            int hrF = c.get(HOUR);
            int minF = c.get(MINUTE);
            TimePickerDialog timePickerDialog2 = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    edtHorarioFim.setText(ajustarHoraMinuto(i, i1));
                }
            }, hrF, minF, true);

            imgSelecionarHorarioFim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    timePickerDialog2.show();

                }
            });
        }else{
            
            TextView txtDePara = findViewById(R.id.txtDePara);

            edtHorarioProposta.setEnabled(false);
            imgSelecionarDataProposta.setVisibility(View.GONE);
            txtDataPropostaEscolhida.setEnabled(false);
            edtLocalProposta.setEnabled(false);
            edtCacheProposta.setEnabled(false);
            edtDescricaoProposta.setEnabled(false);
            btnVoltarProposta = findViewById(R.id.btnVoltarProposta);
            edtHorarioFim.setEnabled(false);
            imgSelecionarHorarioInicio.setVisibility(View.GONE);
            imgSelecionarHorarioFim.setVisibility(View.GONE);
            btnRecusarProposta.setVisibility(View.VISIBLE);

            //vai substituir por um objeto

            PropostaEntity p = (PropostaEntity) getIntent().getSerializableExtra("objetoProposta");
            btnEnviarProposta.setText("ACEITAR");

            edtHorarioProposta.setText(p.getHorarioInicio());
            edtHorarioFim.setText(p.getHorarioFim());
            edtCacheProposta.setText(""+p.getCache());
            edtDescricaoProposta.setText(p.getDescricao());
            edtLocalProposta.setText(p.getLocal());
            txtDataPropostaEscolhida.setText(p.getDataEnvioProposta());

            txtDePara.setText("De: ");
            lblNomeDestinatario.setText(p.getIdEstabelecimento());

            btnEnviarProposta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dao.atualizarProposta(p.getIdProposta(), p.getIdBanda(), p.getIdEstabelecimento(), StatusProposta.ACEITO.name(), TelaProposta.this);
                }
            });

            btnRecusarProposta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dao.atualizarProposta(p.getIdProposta(), p.getIdBanda(), p.getIdEstabelecimento(), StatusProposta.RECUSADO.name(), TelaProposta.this);
                }
            });

            btnVoltarProposta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        String dia = i2 < 10 ? "0" + i2 : "" + i2;
        i1 += 1;
        String mes = i1 < 10 ? "0" + i1 : "" + i1;

        txtDataPropostaEscolhida.setText(dia + "/" + mes + "/" + i);


    }

    private String ajustarHoraMinuto(int hora, int minuto){
        String h = hora < 10 ? "0" + hora : ""+ hora;
        String m = minuto < 10 ? "0" + minuto : "" + minuto;
        return h +":"+m;

    }

    private boolean validaCampos() {
        boolean res = false;
        String txtDPE = txtDataPropostaEscolhida.getText().toString();
        String edtLP = edtLocalProposta.getText().toString();
        String edtDP = edtDescricaoProposta.getText().toString();
        String edtCP = edtCacheProposta.getText().toString();
        String horarioInicio = edtHorarioProposta.getText().toString();
        String horarioFim = edtHorarioFim.getText().toString();

        if (res = isCampoVazio(txtDPE)) {
            txtDataPropostaEscolhida.requestFocus();
        } else if (res = isCampoVazio(edtLP)) {
            edtLocalProposta.requestFocus();
        } else if (res = isCampoVazio(edtDP)) {
            edtDescricaoProposta.requestFocus();
        } else if (res = isCampoVazio(edtCP)) {
            edtCacheProposta.requestFocus();
        }else if (res = isCampoVazio(horarioInicio)){
            imgSelecionarHorarioInicio.requestFocus();
        }else if(res = isCampoVazio(horarioFim)){
            imgSelecionarHorarioFim.requestFocus();
        }

        return res;

    }

    private boolean isCampoVazio(String valor) {
        boolean resultado = (TextUtils.isEmpty(valor) || valor.trim().isEmpty());
        return resultado;
    }

    //TODO arrumar
    private boolean validaHorario(){
        /*boolean res=false;
        String horarioInicio = edtHorarioProposta.getText().toString();
        String horarioFim = edtHorarioFim.getText().toString();

        if(horarioInicio==horarioFim){
            return false;
        }*/
        return true;
    }

    private boolean validaCache() {
        boolean res = false;
        String cache = edtCacheProposta.getText().toString().trim().replace(",", ".");
        Double vlrcache = Double.parseDouble(cache);

        if (vlrcache <= 0.0) {
            res = true;
            edtCacheProposta.requestFocus();
        } else res = false;

        return res;
    }

    private PropostaEntity prepararObjeto(){
        return new PropostaEntity(
                getIntent().getStringExtra("labelDestinatario")
                ,getIntent().getStringExtra("nomeEstabelecimento")
                ,StatusProposta.ABERTO.name()
                ,edtHorarioProposta.getText().toString()
                ,edtHorarioFim.getText().toString()
                ,edtLocalProposta.getText().toString()
                ,edtDescricaoProposta.getText().toString()
                ,Double.parseDouble(edtCacheProposta.getText().toString().trim().replace(",", "."))
                ,txtDataPropostaEscolhida.getText().toString()
                ,DefinirDatas.dataAtual()
                ,false
                ,false
        );
    }

}
