package com.example.meroPASAL.service.recommendation;

import com.example.meroPASAL.Repository.UserPreferenceRepo;
import com.example.meroPASAL.model.UserPreference;
import com.example.meroPASAL.security.userModel.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserPreferenceRepo userPreferenceRepo;

    // ================= AI LEARNING =================
    public void updatePreference(Customer customer, String type, String value, int increment) {

        if (customer == null || value == null) return;

        var prefOpt = userPreferenceRepo
                .findByCustomerAndTypeAndValue(customer, type, value.toLowerCase());

        if (prefOpt.isPresent()) {
            UserPreference pref = prefOpt.get();
            pref.setWeight(pref.getWeight() + increment);
            userPreferenceRepo.save(pref);
        } else {
            UserPreference pref = new UserPreference();
            pref.setCustomer(customer);
            pref.setType(type);
            pref.setValue(value.toLowerCase());
            pref.setWeight(increment);
            userPreferenceRepo.save(pref);
        }
    }

    // ================= FETCH AI MEMORY =================
    public List<UserPreference> getPreferences(Customer customer) {
        return userPreferenceRepo.findByCustomer(customer);
    }
}

// langchain and spring ai