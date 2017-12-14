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

    @Value("${ignoreConcepts}")
    private String ignoreConceptName;

    @Autowired
    private ObsService obsService;
    private List<Concept> allMultiSelectConcepts;
    private List<Concept> ignoreConcepts;


    public BahmniForm createForm(Concept concept, BahmniForm parentForm) {
        return createForm(concept, parentForm, 0);
    }

    private BahmniForm createForm(Concept concept, BahmniForm parentForm, int depth) {
        BahmniForm bahmniForm = new BahmniForm();
        bahmniForm.setFormName(concept);
        bahmniForm.setDepthToParent(depth);
        bahmniForm.setParent(parentForm);
        bahmniForm.setRootForm(getRootFormFor(parentForm));

        constructFormFields(concept, bahmniForm, depth);

        return bahmniForm;
    }

    private BahmniForm getRootFormFor(BahmniForm form) {
        if (form == null) {
            return null;
        } else if (form.getDepthToParent() == 1) {
            return form;
        }
        return getRootFormFor(form.getParent());
    }

    private void constructFormFields(Concept concept, BahmniForm bahmniForm, int depth) {
        if (concept.getIsSet() == 0) {
            bahmniForm.addField(concept);
            return;
        }

        List<Concept> childConcepts = obsService.getChildConcepts(concept.getName());
        int childDepth = depth + 1;
        for (Concept childConcept : childConcepts) {
            if (ignoreConcepts.contains(childConcept)){
                continue;
            } else if (childConcept.getIsSet() == 0 && !allMultiSelectConcepts.contains(childConcept)) {
                bahmniForm.addField(childConcept);
            } else {
                bahmniForm.addChild(createForm(childConcept, bahmniForm, childDepth));

            }
        }
    }

    @PostConstruct
    public void postConstruct() {
        this.allMultiSelectConcepts = obsService.getAllMultiSelectConcepts();
        this.ignoreConcepts = obsService.getConceptsByNames(ignoreConceptName);
    }

    public void setObsService(ObsService obsService) {
        this.obsService = obsService;
    }
}
