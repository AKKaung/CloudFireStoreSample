package com.asmaahamed.cloudfirestoresample.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asmaahamed.cloudfirestoresample.Entity.Note;
import com.asmaahamed.cloudfirestoresample.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class AddLoadNotes extends AppCompatActivity {

    private static final String TAG = "AddLoadNotes";
    private static final String KEY_TITLE = "edittextTitle";
    private static final String KEY_DESCRIPTION="edittextDescription";


    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Notebook");
    private DocumentSnapshot lastResult;

    EditText noteTitle , noteDescription , proirityEdittext;
    Button addButton , loadNotesButton;
    TextView showNotes;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_load_notes);

        initiate();
        executeBatchedWrite();
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteListener();

    }


    public void noteListener(){
        notebookRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if( e !=null){
                    return;
                }
                String data ="";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Note note = documentSnapshot.toObject(Note.class);
                    note.setDocumentId(documentSnapshot.getId());

                    String documentId =note.getDocumentId();
                    String title = note.getTitle();
                    String description = note.getDescription();
                    int proirity =note.getProirity();

                    data += "ID:"+documentId+
                            "\nTitle :"+title+"\nDescription: "+description+"\nProirity: "+proirity+"\n\n";
                }
                showNotes.setText(data);

            }
        });

    }

    private void initiate() {

        noteTitle=findViewById(R.id.note_title_editText);
        noteDescription=findViewById(R.id.note_description_editText);
        showNotes=findViewById(R.id.showNotes_textView);
        proirityEdittext =findViewById(R.id.proirity_editText);
        addButton=findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNotes();
            }
        });
        loadNotesButton=findViewById(R.id.LoadNotes_button);
        loadNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNotes();
            }
        });
    }

    public void addNotes(){

        String title = noteTitle.getText().toString();
        String description = noteDescription.getText().toString();

        if (proirityEdittext.length()==0){
            proirityEdittext.setText("0");
        }
        int proirity =Integer.parseInt(proirityEdittext.getText().toString());

        Note note = new Note(title,description,proirity);

        notebookRef.add(note)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(AddLoadNotes.this, "document added", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void loadNotes(){

       Task task1= notebookRef.whereEqualTo("proirity",2)
                .orderBy("proirity", Query.Direction.DESCENDING)
                .limit(1)
                .get();


        Task task2= notebookRef.whereEqualTo("proirity",2)
                .orderBy("proirity", Query.Direction.DESCENDING)
                .get();


       Task<List<QuerySnapshot>> allTasks =Tasks.whenAllSuccess(task1,task2);

                allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                    @Override
                    public void onSuccess(List<QuerySnapshot> querySnapshots) {
                        String data = "";
                        for (QuerySnapshot queryDocumentSnapshots :querySnapshots )
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentId(documentSnapshot.getId());

                            String documentId=note.getDocumentId();
                            String title = note.getTitle();
                            String description =note.getDescription();
                            int proirity =note.getProirity();

                            data += "ID:"+documentId+
                                    "\nTitle: "+title+"\nDescription: "+description+"\nProirity: "+proirity+"\n\n";
                        }
                        showNotes.setText(data);

                    }
                });
    }

    private void executeBatchedWrite() {
        WriteBatch batch = db.batch();
        DocumentReference doc1 = notebookRef.document("New Document");
        batch.set(doc1, new Note("Android developer", "programming job", 4));

        DocumentReference doc2 = notebookRef.document("BBfxK92HARoUxc6gOMaT");
        batch.update(doc2, "title", "Updated Note");

        DocumentReference doc3 = notebookRef.document("\n" + "DoZnoZEuKJGvFLjCg1ya");
        batch.delete(doc3);

        DocumentReference doc4 = notebookRef.document();
        batch.set(doc4, new Note("IOS developer", "programming job", 1));

        batch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showNotes.setText(e.toString());
            }
        });
    }
     //Method to icrease peroirity each time I open and close
    private void executeTransaction() {
        db.runTransaction(new Transaction.Function<Long>() {
            @Override
            public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference exampleNoteRef = notebookRef.document("Example Note");
                DocumentSnapshot exampleNoteSnapshot = transaction.get(exampleNoteRef);
                long newPriority = exampleNoteSnapshot.getLong("priority") + 1;
                transaction.update(exampleNoteRef, "priority", newPriority);
                return newPriority;
            }
        }).addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long result) {
                Toast.makeText(AddLoadNotes.this, "New Priority: " + result, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
