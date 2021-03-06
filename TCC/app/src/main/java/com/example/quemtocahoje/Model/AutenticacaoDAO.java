package com.example.quemtocahoje.Model;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.quemtocahoje.Adapter.AutenticacaoDTOAdapter;
import com.example.quemtocahoje.DTO.AutenticacaoDTO;
import com.example.quemtocahoje.Enum.TabelasFirebase;
import com.example.quemtocahoje.Enum.TipoUsuario;
import com.example.quemtocahoje.Persistencia.Entity.AutenticacaoEntity;
import com.example.quemtocahoje.Persistencia.Entity.EspectadorEntity;
import com.example.quemtocahoje.Persistencia.Entity.EstabelecimentoEntity;
import com.example.quemtocahoje.Persistencia.Entity.MusicoEntity;
import com.example.quemtocahoje.Utility.EncodeBase64;
import com.example.quemtocahoje.Utility.Mensagem;
import com.example.quemtocahoje.Views.TelaInicial;
import com.example.quemtocahoje.Views.TelaInicialEspectador;
import com.example.quemtocahoje.Views.TelaInicialEstabelecimento;
import com.example.quemtocahoje.Views.TelaInicialMusico;
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

import java.util.HashMap;
import java.util.Map;

public class AutenticacaoDAO {
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private Long contador;
    private HashMap<DatabaseReference, ValueEventListener> hashMap;
    private ValueEventListener valueEventListener;

    public AutenticacaoDAO(FirebaseDatabase database, DatabaseReference reference, FirebaseUser firebaseUser) {
        this.database = database;
        this.reference = reference;
        this.firebaseUser = firebaseUser;
    }

    public AutenticacaoDAO(){hashMap = new HashMap<>();}

    public void autenticar(final String login, final String senha, final FirebaseAuth auth, final Context ctx){
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(ctx);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Autenticando usu??rio");
        progressDialog.setTitle("Aguarde");
        progressDialog.show();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(TabelasFirebase.Autenticacao.name());
        databaseReference.addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                hashMap.put(databaseReference, valueEventListener);
                setContador(0L);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){//para cada usuario no autenticacao, verificar login/senha para
                    final AutenticacaoEntity entidade = snapshot.getValue(AutenticacaoEntity.class);
                    setContador(getContador()+1);

                    if((entidade.getLogin().equals(login) || entidade.getEmail().equals(login)) && entidade.getSenha().equals(senha))
                    {
                        removeValueEventListener(hashMap);
                        auth.signInWithEmailAndPassword(entidade.getEmail(),senha)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            FirebaseUser user = auth.getCurrentUser();
                                            if (entidade.getTipoUsuario().equals(TipoUsuario.MUSICO.name())) {
                                                loginMusico(EncodeBase64.toBase64(entidade.getEmail()), ctx);
                                            } else if (entidade.getTipoUsuario().equals(TipoUsuario.ESTABELECIMENTO.name())) {
                                                loginEstabelecimento(EncodeBase64.toBase64(entidade.getEmail()), ctx);
                                            } else if (entidade.getTipoUsuario().equals(TipoUsuario.ESPECTADOR.name())) {
                                                loginEspectador(EncodeBase64.toBase64(entidade.getEmail()), ctx);
                                            }
                                        }
                                    }
                                });
                        break;
                    }else if(getContador() == dataSnapshot.getChildrenCount()){
                        progressDialog.dismiss();
                        Mensagem.notificar(ctx, "Usu??rio Inv??lido", "Login e/ou senha incorretos.");
                        removeValueEventListener(hashMap);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Mensagem.notificar(ctx, "Erro na aplica????o", "Ocorreu um erro ao efetuar o login do usu??rio.");
                Log.d("ERRO FIREBASE", databaseError.getDetails());
                removeValueEventListener(hashMap);
            }
        });
    }

    public void loginEspectador(final String id, final Context ctx){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(TabelasFirebase.Usuarios.name());
        databaseReference = databaseReference.child(TipoUsuario.ESPECTADOR.name()).child(id);

        databaseReference.addListenerForSingleValueEvent(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               removeValueEventListener(hashMap);
                if(dataSnapshot.getValue() != null)
                {
                    EspectadorEntity entidade = dataSnapshot.getValue(EspectadorEntity.class);

                    AutenticacaoDTO dto = AutenticacaoDTOAdapter.espectadorToAutenticacaoDTO(entidade, EncodeBase64.fromBase64(id));
                    Intent telaInicialEspectador = new Intent(ctx, TelaInicialEspectador.class);
                    telaInicialEspectador.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    telaInicialEspectador.putExtra("dtoAutenticacao", dto);
                    ctx.startActivity(telaInicialEspectador );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                removeValueEventListener(hashMap);
            }
        });
    }

    public void loginMusico(final String id, final Context ctx){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(TabelasFirebase.Usuarios.name()).child(TipoUsuario.MUSICO.name()).child(id);
        databaseReference.addListenerForSingleValueEvent(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hashMap.put(databaseReference, valueEventListener);
                if(dataSnapshot.getValue()!= null)
                {
                    MusicoEntity entidade = dataSnapshot.getValue(MusicoEntity.class);
                    if (entidade.getAutenticacao_id().equals(id))
                    {
                        removeValueEventListener(hashMap);
                        Log.d("DENTROUSUARIO",entidade.getNome());
                        Log.d("DENTROUSUARIO",entidade.getNomeArtistico());

                        AutenticacaoDTO dto = AutenticacaoDTOAdapter.musicoToAutenticacaoDTO(entidade, EncodeBase64.fromBase64(id));
                        Intent telaInicialMusico = new Intent(ctx, TelaInicialMusico.class);
                        telaInicialMusico.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        telaInicialMusico.putExtra("dtoAutenticacao",dto);
                        ctx.startActivity(telaInicialMusico);
                    }
                }
                removeValueEventListener(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                removeValueEventListener(hashMap);
            }
        });
    }

    public void loginEstabelecimento(final String id, final Context ctx){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(TabelasFirebase.Usuarios.name()).child(TipoUsuario.ESTABELECIMENTO.name()).child(id);
        databaseReference.addListenerForSingleValueEvent(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hashMap.put(databaseReference, valueEventListener);
                if(dataSnapshot.getValue()!=null)
                {
                    EstabelecimentoEntity entidade = dataSnapshot.getValue(EstabelecimentoEntity.class);
                    if (entidade.getAutenticacao_id().equals(id))
                    {
                        removeValueEventListener(hashMap);
                        Log.d("DENTROUSUARIO",entidade.getNomeDono());
                        Log.d("DENTROUSUARIO",entidade.getNomeFantasia());

                        AutenticacaoDTO dto = AutenticacaoDTOAdapter.estabelecimentoToAutenticacaoDTO(entidade, EncodeBase64.fromBase64(id));
                        Intent telaInicialEstabelecimento = new Intent(ctx, TelaInicialEstabelecimento.class);
                        telaInicialEstabelecimento.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        telaInicialEstabelecimento.putExtra("dtoAutenticacao",dto);
                        ctx.startActivity(telaInicialEstabelecimento);
                    }
                }
                removeValueEventListener(hashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                removeValueEventListener(hashMap);
            }
        });
    }

   /* public AutenticacaoDTO recuperarAutenticacao(String login, final String senha){
        final AutenticacaoDTO dto = new AutenticacaoDTO();
        final DatabaseReference autenticacaoRef = database.getReference("Autenticacao");
        //autenticacaoRef.orderByChild("id").equalTo(login).equalTo(senha).limitToFirst(1).addValueEventListener(new ValueEventListener() {
        String id = firebaseUser.getUid();
        autenticacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                Log.d("MOCK","" + dataSnapshot.getChildrenCount());
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    AutenticacaoEntity entidade = snapshot.getValue(AutenticacaoEntity.class);
                    Log.d("MOCK",entidade.getLogin());
                    Log.d("MOCK",entidade.getSenha());
                    Log.d("MOCK",entidade.getTipoUsuario());
                    if(entidade.getId().equals(firebaseUser.getUid()))
                    {

                        dto.setIdAutenticacao(entidade.getDataCriacao().toString());
                        dto.setNome(entidade.getLogin());
                        dto.setSenha(entidade.getSenha());
                        dto.setTipoUsuario(entidade.getTipoUsuario());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        return dto;
    }*/

    public Long getContador() {
        return contador;
    }

    public void setContador(Long contador) {
        this.contador = contador;
    }

    public static void removeValueEventListener(HashMap<DatabaseReference, ValueEventListener> hashMap) {
        for (Map.Entry<DatabaseReference, ValueEventListener> entry : hashMap.entrySet()) {
            DatabaseReference databaseReference = entry.getKey();
            ValueEventListener valueEventListener = entry.getValue();
            databaseReference.removeEventListener(valueEventListener);
        }
    }

}
