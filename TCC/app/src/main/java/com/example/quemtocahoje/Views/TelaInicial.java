package com.example.quemtocahoje.Views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quemtocahoje.Model.AutenticacaoDAO;
import com.example.quemtocahoje.DTO.AutenticacaoDTO;
import com.example.quemtocahoje.Enum.TipoUsuario;
import com.example.quemtocahoje.Persistencia.Entity.AutenticacaoEntity;
import com.example.quemtocahoje.Utility.AESCrypt;

import com.example.quemtocahoje.Utility.Mensagem;
import com.example.tcc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TelaInicial extends AppCompatActivity{

    private Button btnLogin;
    private TextView txtCadastro;
    private TextView txtEsqueceuSuaSenha;
    private EditText edtLogin;
    private EditText edtSenha;

    private AutenticacaoDAO dao;

    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);
        getSupportActionBar().hide(); // Esconde a label

        final Intent telaCadastroEspectador = new Intent(this, TelaCadastroEspectador.class);
        final Intent telaEsqueciSenha = new Intent(this, TelaEsqueceuaSenha.class);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        dao = new AutenticacaoDAO();

        btnLogin = findViewById(R.id.btnLogin);
        txtCadastro = findViewById(R.id.txtCadastro);
        txtEsqueceuSuaSenha = findViewById(R.id.txtEsqueceuSuaSenha);
        edtLogin = findViewById(R.id.edtLogin);
        edtSenha = findViewById(R.id.edtSenha);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isCamposValidos(edtLogin, edtSenha)){
                    try
                    {
                        AutenticacaoDAO a = new AutenticacaoDAO();
                        final String s = AESCrypt.encrypt(edtSenha.getText().toString());
                        a.autenticar(edtLogin.getText().toString(),s,auth,TelaInicial.this);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    //TODO atualizar ??ltimo login no Firebase
                    }else {
                        Mensagem.notificar(TelaInicial.this, "Campos Inv??lidos", "Um ou mais campos n??o foram preenchidos corretamente");
                    }
            }
        });

        txtCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //passando da tela inicial para a segunda tela

                startActivity (telaCadastroEspectador);
            }
        });

        txtEsqueceuSuaSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(telaEsqueciSenha);
            }
        });

    }

    private boolean isCamposValidos(EditText l, EditText s){
        if(l == null || l.getText().toString().trim().equals("")
           || s == null || s.getText().toString().trim().equals(""))
            return false;

        return true;
    }

}
