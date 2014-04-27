package edu.tum.cs.cadmos.analysis.ui.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.ui.editor.GlobalURIEditorOpener;
import org.eclipse.xtext.ui.resource.IResourceUIServiceProvider;

import com.google.inject.Inject;

import edu.tum.cs.cadmos.analysis.ui.views.architecture.ArchitectureViewPart;
import edu.tum.cs.cadmos.language.cadmos.Deployment;
import edu.tum.cs.cadmos.language.cadmos.Embedding;

public class ModelElementLinker {
	
	public static ModelElementLinker INSTANCE = new ModelElementLinker();

	@Inject
	private GlobalURIEditorOpener gleo;
	private IResourceServiceProvider.Registry rspr = 
			  IResourceServiceProvider.Registry.INSTANCE;
		
		public void findEmbedding(String fname){
			Deployment context = getContext();
			System.out.println("searching: "+fname);
			System.out.println("in context "+context);
			visited.clear();
			traverse(context, "");
//			System.out.println(context.eCrossReferences());
//			System.out.println(context.eContents());
			System.out.println("##############");
			for(Entry e : map.entrySet()){
				System.out.println(e);
				if (e.getKey().equals("writer")){
						Embedding em = (Embedding) e.getValue();
						System.out.println(EcoreUtil.getURI(em));
						
						
						
						
						IResourceServiceProvider sp = rspr.getResourceServiceProvider(EcoreUtil.getURI(em));
//						sp.
						gleo.open( EcoreUtil.getURI(em), true);
				}
			}
			System.out.println("##############");
		}
		
		private Deployment getContext() {
			return ArchitectureViewPart.dep;
		}
		
		private HashMap<String, EObject> map = new HashMap<String, EObject> ();
		private HashSet<EObject> visited = new HashSet<EObject> ();
		private void traverse(EObject eo, String indent){
			if(visited.contains(eo)){
				return;
			}
			visited.add(eo);
			
			if (eo instanceof Embedding) {
				Embedding em = (Embedding) eo;
				map.put(em.getName(), em);
			}
			
			ArrayList<EObject> children = new ArrayList<EObject>();
			children.addAll(eo.eContents());
			children.addAll(eo.eCrossReferences());
			System.out.println(indent+eo);
			for(EObject child : children){
				traverse(child, indent+"\t");
			}
		}
		
	}



//.filter[it instanceof Embedding].filter[(it as Embedding).name == fname].map[s | println(s)]