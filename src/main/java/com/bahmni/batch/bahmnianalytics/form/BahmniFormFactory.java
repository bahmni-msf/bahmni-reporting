package com.bahmni.batch.bahmnianalytics.form;

import com.bahmni.batch.bahmnianalytics.form.domain.BahmniForm;
import com.bahmni.batch.bahmnianalytics.form.domain.Concept;
import com.bahmni.batch.bahmnianalytics.form.service.ObsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class BahmniFormFactory {

    @Value("${addMoreAndMultiSelectConcepts}")
    private String addMoreConceptNames;

    @Autowired
    private ObsService obsService;
    private List<Concept> allMultiSelectConcepts;


    public BahmniForm createForm(Concept concept, BahmniForm parentForm) {
        return createForm(concept, parentForm, 0);
    }

    private BahmniForm createForm(Concept concept, BahmniForm parentForm, int depth) {
        BahmniForm bahmniForm = new BahmniForm();
        bahmniForm.setFormName(concept);
        bahmniForm.setDepthToParent(depth);
        bahmniForm.setParent(parentForm);

        constructFormFields(concept, bahmniForm, 0);

        return bahmniForm;
    }

    private void constructFormFields(Concept concept, BahmniForm bahmniForm, int depth) {
        if (concept.getIsSet() == 0) {
            bahmniForm.addField(concept);
            return;
        }

        List<Concept> childConcepts = obsService.getChildConcepts(concept.getName());
        int childDepth = depth + 1;
        for (Concept childConcept : childConcepts) {
            if (childConcept.getIsSet() == 0 && !allMultiSelectConcepts.contains(childConcept)) {
                bahmniForm.addField(childConcept);
            } else {
                bahmniForm.addChild(createForm(childConcept, bahmniForm, childDepth));

            }
        }
    }

    @PostConstruct
    public void postConstruct() {
        this.allMultiSelectConcepts = obsService.getAllMultiSelectConcepts();
    }

    public void setObsService(ObsService obsService) {
        this.obsService = obsService;
    }
}