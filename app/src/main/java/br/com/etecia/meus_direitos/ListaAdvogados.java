package br.com.etecia.meus_direitos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.com.etecia.meus_direitos.objetos.API_IBGE;
import br.com.etecia.meus_direitos.objetos.Advogados;
import br.com.etecia.meus_direitos.objetos.Cidades;
import br.com.etecia.meus_direitos.objetos.Estados;
import br.com.etecia.meus_direitos.objetos.Subdistritos;

public class ListaAdvogados extends AppCompatActivity {

    MaterialToolbar toolbar;
    Spinner spinnerAreas, spinnerEstados, spinnerCidades, spinnerSubdistritos;
    ImageView filtro;
    CardView cardViewFiltro, cardViewAdvogados;
    RecyclerAdapter mAdapter;
    Advogados advogados;
    List<Advogados> advogadosList;

    String url = "https://appmeusdireitos.000webhostapp.com/PegaDados.php";
    Cidades[] municipios = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_advogados);

        spinnerAreas = findViewById(R.id.filtrarArea);
        spinnerEstados = findViewById(R.id.filtrarEstado);
        spinnerCidades = findViewById(R.id.filtrarCidade);
        spinnerSubdistritos = findViewById(R.id.filtrarSubdistritos);

        spinnerCidades.setEnabled(false);
        spinnerSubdistritos.setEnabled(false);

        filtro = findViewById(R.id.filtrar);
        cardViewFiltro = findViewById(R.id.cardFiltro);

        //Colocando os dados do advogados na lista do cardview
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view_lista);
        RecyclerAdapter mAdapter = new RecyclerAdapter(getApplicationContext(), advogadosList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

        //Criando um array de areas de trabalho no spinner
        ArrayAdapter<CharSequence> adapterAreas = ArrayAdapter.createFromResource(this, R.array.areas_atuacao, android.R.layout.simple_spinner_item);
        adapterAreas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAreas.setAdapter(adapterAreas);

        String respostaEstados = executaApiIBGE("estado");

        Gson gsonEstados = new GsonBuilder().setPrettyPrinting().create();
        final Estados[] estados = gsonEstados.fromJson(String.valueOf(respostaEstados), Estados[].class);

        final ArrayList<String> estadosParaSpinner = new ArrayList<>();

        for (Estados estado: estados){
            estadosParaSpinner.add(estado.getNome());
        }

        //Ordena em ordem alfabética
        Collections.sort(estadosParaSpinner);

        estadosParaSpinner.add(0, "Selecione o Estado");

        //Criando um array de estados no spinner
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estadosParaSpinner);
        spinnerEstados.setAdapter(adapterEstados);

        final String[] areasAtuacao = new String[9];

        spinnerAreas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (Estados estado : estados) {
                    if (estado.getNome().equals(spinnerEstados.getSelectedItem().toString())) {
                        solicitarMunicipios(estado.getSigla());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCidades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (Cidades cidade : municipios) {
                    if (cidade.getNome().equals(spinnerCidades.getSelectedItem().toString())) {
                        solicitarSubdistritos(String.valueOf(cidade.getId()));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final int[] clique = {1};

        filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (clique[0] == 1) {
                    cardViewFiltro.setVisibility(View.VISIBLE);
                    clique[0] = 0;

                } else {
                    cardViewFiltro.setVisibility(View.GONE);
                    clique[0] = 1;
                }
            }
        });


        toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListaAdvogados.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        getData();
    }

    private void getData() {

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                advogadosList.clear();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String sucesso = jsonObject.getString("Sucesso");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    if (sucesso.equals("1")){
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject object = jsonArray.getJSONObject(i);

                            int id = Integer.parseInt(object.getString("id"));
                            int imagem = Integer.parseInt(object.getString("imagem"));
                            String nomeAdvogado = object.getString("nomeAdvogado");
                            String estado = object.getString("estado");
                            String cidade = object.getString("cidade");
                            String areaAtuacao = object.getString("areaAtuacao");



                            advogados = new Advogados(id, imagem, nomeAdvogado, estado, cidade, areaAtuacao);
                            advogadosList.add(advogados);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                }catch (Exception e){

                }

                //Toast.makeText(ListaAdvogados.this, response.toString(), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ListaAdvogados.this, error.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(ListaAdvogados.this);
        requestQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_navegacao_cliente, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.advogados:
                Intent intent = new Intent(getApplicationContext(), ListaAdvogados.class);
                startActivity(intent);
                break;

            case R.id.informacoes:
                Intent intent2 = new Intent(getApplicationContext(), InformacoesCli.class);
                startActivity(intent2);
                break;
        }
        return true;
    }

    private void solicitarMunicipios(String siglaEstado) {
        String respostaMunicipios = executaApiIBGE("municipio", siglaEstado);

        Gson gsonMunicipios = new GsonBuilder().setPrettyPrinting().create();
        municipios = gsonMunicipios.fromJson(String.valueOf(respostaMunicipios), Cidades[].class);

        final ArrayList<String> municipiosParaSpinner = new ArrayList<>();
        final ArrayList<String> idMunicipios = new ArrayList<>();

        for (Cidades cidade: municipios){
            municipiosParaSpinner.add(cidade.getNome());
            idMunicipios.add(String.valueOf(cidade.getId()));
        }

        //Ordena em ordem alfabética
        Collections.sort(municipiosParaSpinner);

        municipiosParaSpinner.add(0, "Selecione a Cidade");

        spinnerCidades.setEnabled(true);

        //Criando um array de municipios no spinner
        ArrayAdapter<String> adapterMunicipios = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, municipiosParaSpinner);
        spinnerCidades.setAdapter(adapterMunicipios);

    }

    private void solicitarSubdistritos(String idMunicipio) {
        String respostaSubdistritos = executaApiIBGE("subdistrito", idMunicipio);

        Gson gsonSubdistritos = new GsonBuilder().setPrettyPrinting().create();
        Subdistritos[] subdistritos = gsonSubdistritos.fromJson(String.valueOf(respostaSubdistritos), Subdistritos[].class);

        final ArrayList<String> subdistritosParaSpinner = new ArrayList<>();

        for (Subdistritos subdistrito: subdistritos){
            subdistritosParaSpinner.add(subdistrito.getNome());
        }

        //Ordena em ordem alfabética
        Collections.sort(subdistritosParaSpinner);

        subdistritosParaSpinner.add(0, "Selecione o Subdistrito");

        spinnerSubdistritos.setEnabled(true);

        //Criando um array de municipios no spinner
        ArrayAdapter<String> adapterSubdistritos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subdistritosParaSpinner);
        spinnerSubdistritos.setAdapter(adapterSubdistritos);

    }

        private String executaApiIBGE (String... params) {
            API_IBGE api_ibge = new API_IBGE();

            String respostaIBGE = null;

            try {
                respostaIBGE = api_ibge.execute(params).get();

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return respostaIBGE;
        }
}