package com.example.quemtocahoje.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.quemtocahoje.DTO.AutenticacaoDTO;
import com.example.quemtocahoje.Enum.TipoUsuario;
import com.example.quemtocahoje.Model.AvaliacaoDAO;
import com.example.quemtocahoje.Model.PropostaDAO;
import com.example.quemtocahoje.Persistencia.Entity.AvaliacaoEstabelecimentoEntity;
import com.example.quemtocahoje.Persistencia.Entity.PropostaEntity;
import com.example.tcc.R;
import com.google.firebase.auth.FirebaseAuth;

public class TelaInicialEstabelecimento extends AppCompatActivity {

    private TextView txtMensagensMusico;
    private TextView txtNomeEstabelecimento;
    private TextView txtPesquisarInicialEstabelecimento;
    private TextView txtPropostasInicialEstabelecimento;
    private TextView txtAgendaInicialEstabelecimento;
    private TextView txtHistoricoInicialEstabelecimento;
    private TextView txtSairInicialEstabelecimento;
    private TextView txtAvaliacaoMusico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial_estabelecimento);
        getSupportActionBar().hide();
        final Intent telaLogin = new Intent(this, TelaInicial.class);
        final Intent telaPesquisaMusico = new Intent(this,TelaPesquisaMusico.class);
        final Intent telaProposta = new Intent(this,TelaProposta.class);
        final Intent telaAvaliacaoMusico = new Intent(this,TelaAvaliacaoMusico.class);
        final Intent telaMensagensAtivas = new Intent(this,TelaMensagensAtivas.class);

        txtMensagensMusico = findViewById(R.id.txtMensagensMusico);
        txtNomeEstabelecimento = findViewById(R.id.txtNomeEstabelecimento);
        txtPesquisarInicialEstabelecimento = findViewById(R.id.txtPesquisarInicialEstabelecimento);
        txtPropostasInicialEstabelecimento = findViewById(R.id.txtPropostasInicialEstabelecimento);
        txtAgendaInicialEstabelecimento = findViewById(R.id.txtAgendaInicialEstabelecimento);
        txtHistoricoInicialEstabelecimento = findViewById(R.id.txtHistoricoInicialEstabelecimento);
        txtSairInicialEstabelecimento = findViewById(R.id.txtSairInicialEstabelecimento);
        txtAvaliacaoMusico = findViewById(R.id.txtAvaliacaoMusico);

        txtNomeEstabelecimento.setText("Ol?? " + preencherNomeUsuario() + "!");
        AutenticacaoDTO dto = (AutenticacaoDTO) getIntent().getSerializableExtra("dtoAutenticacao");

        txtMensagensMusico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaMensagensAtivas.putExtra("AutenticacaoDTO",dto);
                startActivity(telaMensagensAtivas);
            }
        });

        txtPesquisarInicialEstabelecimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaPesquisaMusico.putExtra("dtoAutenticacao",dto);
                startActivity(telaPesquisaMusico);
            }
        });

        txtSairInicialEstabelecimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(telaLogin);
                finishAffinity();
            }
        });

        txtAgendaInicialEstabelecimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PropostaDAO dao = new PropostaDAO();
                dao.recuperarEventos(dto.getNome(), TipoUsuario.ESTABELECIMENTO.name(), "AGENDA", TelaInicialEstabelecimento.this,getIntent());//estab

                //Intent telaagendausuarios = new Intent(TelaInicialEstabelecimento.this,TelaAgendaUsuarios.class);
                //telaagendausuarios.putExtra("dtoAutenticacao",dto);
                //startActivity(telaagendausuarios);
            }
        });

        txtPropostasInicialEstabelecimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(telaProposta);
            }
        });

        txtAvaliacaoMusico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AvaliacaoDAO dao = new AvaliacaoDAO();
                dao.recuperarListaAvaliacoesPendentes(preencherNomeUsuario(), TipoUsuario.ESTABELECIMENTO.name(),TelaInicialEstabelecimento.this);//estab
            }
        });

        txtHistoricoInicialEstabelecimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PropostaDAO dao = new PropostaDAO();
                dao.recuperarEventos(preencherNomeUsuario(), TipoUsuario.ESTABELECIMENTO.name(), "HISTORICO", TelaInicialEstabelecimento.this,getIntent());//estab

            }
        });
    }

    private String preencherNomeUsuario()
    {
        AutenticacaoDTO dto = (AutenticacaoDTO) getIntent().getSerializableExtra("dtoAutenticacao");
        return dto.getNome();
    }
}
