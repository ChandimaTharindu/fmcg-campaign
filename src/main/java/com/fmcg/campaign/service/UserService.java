package com.fmcg.campaign.service;

import com.fmcg.campaign.dto.UserRequest;
import com.fmcg.campaign.dto.UserResponse;
import com.fmcg.campaign.entity.AppUser;
import com.fmcg.campaign.exception.BadRequestException;
import com.fmcg.campaign.exception.ResourceNotFoundException;
import com.fmcg.campaign.repository.AppUserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final AppUserRepository appUserRepository;

    public UserResponse createUser(UserRequest request) {
        if (request.userName() == null || request.userName().isBlank()) {
            throw new BadRequestException("userName is required");
        }

        if (request.contactNumber() == null || request.contactNumber().isBlank()) {
            throw new BadRequestException("contactNumber is required");
        }

        AppUser user = new AppUser();
        user.setId(request.id());
        user.setUserName(request.userName());
        user.setContactNumber(request.contactNumber());

        AppUser saved = appUserRepository.save(user);
        return new UserResponse(saved.getId(), saved.getUserName(), saved.getContactNumber());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        return new UserResponse(user.getId(), user.getUserName(), user.getContactNumber());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        return appUserRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getUserName(), user.getContactNumber()))
                .toList();
    }
}
