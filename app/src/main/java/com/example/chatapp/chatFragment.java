package com.example.chatapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;


public class chatFragment extends Fragment {

   FirebaseFirestore firebaseFirestore;
   LinearLayoutManager  linearLayoutManager;
   FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
   ImageView imageView;
    FirestoreRecyclerAdapter<FirebaseModel,NoteViewHolder> chatAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chat, container, false);
        firebaseAuth =FirebaseAuth.getInstance();
        firebaseFirestore =FirebaseFirestore.getInstance();
        recyclerView= view.findViewById(R.id.recyclerView);
        Query query= firebaseFirestore.collection("Users").whereNotEqualTo("uid",firebaseAuth.getUid());

        FirestoreRecyclerOptions<FirebaseModel> allUser =new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query,FirebaseModel.class).build();
        chatAdapter=new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(allUser) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull FirebaseModel model) {

                holder.username_chat.setText(model.getName());
                String uri =model.getImage();

                Picasso.get().load(uri).into(imageView);
                if(model.getStatus().equals("Online"))
                {
                    holder.userStatus_chat.setText(model.getStatus() );
                    holder.userStatus_chat.setTextColor(Color.GREEN);
                }
                else
                {
                    holder.userStatus_chat.setText(model.getStatus() );
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(),specificchat.class);
                        intent.putExtra("name",model.getName());
                        intent.putExtra("receiveruid",model.getUid());
                        intent.putExtra("imageuri",model.getImage());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.chatview,parent, false);
                return  new NoteViewHolder(v);
            }
        };

        recyclerView.setHasFixedSize(true);
        linearLayoutManager =new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(chatAdapter  );
        return view;



    }

    public  class NoteViewHolder extends RecyclerView.ViewHolder
    {

        TextView username_chat;
        TextView userStatus_chat;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            username_chat = itemView.findViewById(R.id.chatname);
            userStatus_chat = itemView.findViewById(R.id.chatstatus);
            imageView=itemView.findViewById(R.id.chat_dp);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(chatAdapter!=null)
        {
            chatAdapter.stopListening();
        }
    }
}