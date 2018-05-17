package co.com.funnypets.funnypetsmobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.Executor;

import co.com.funnypets.funnypetsmobile.R;
import co.com.funnypets.funnypetsmobile.entities.MensajeEnviar;
import co.com.funnypets.funnypetsmobile.entities.Post;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link subirFotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link subirFotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class subirFotoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FirebaseStorage storage=FirebaseStorage.getInstance();
    private StorageReference refStr;
    private static final int GALERY_INTENT=1;
    private ImageView image;
    private Uri u;

    public subirFotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment subirFotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static subirFotoFragment newInstance(String param1, String param2) {
        subirFotoFragment fragment = new subirFotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_subir_foto, container, false);
        image=view.findViewById(R.id.image_subir_foto);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Image_touch","imagen....");
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent.createChooser(intent,"Seleccione una imagen"),GALERY_INTENT);

            }
        });
        final Spinner spinner = (Spinner) view.findViewById(R.id.categorias_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        Button btnSubirFoto=view.findViewById(R.id.btn_subir_foto);
        btnSubirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refStr = storage.getReference("imagenes_posts");//imagenes_post
                final StorageReference fotoReferencia = refStr.child(u.getLastPathSegment());
                fotoReferencia.putFile(u).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Post post=new Post();
                        post.setDescripcion("");
                        post.setNumOfLikes(0);
                        Log.d("url",spinner.getSelectedItem().toString());
                        post.setUrlphotopost(taskSnapshot.getDownloadUrl().toString());
                        Toast.makeText(getContext(),"Se subio la foto exitosamente ",Toast.LENGTH_SHORT).show();
                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("posts/"+PostFragment.i++);
                      //  ref.child(ref.getKey()).setValue(post);
                       // Log.d("url",ref.);
                        ref.setValue(post);
                    }
                });
                //post.setUsuario(FirebaseAuth.getInstance().getCurrentUser());
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALERY_INTENT && resultCode == -1) {
            u = data.getData();
            image.setImageURI(u);
           /* */
        }
    }
}
