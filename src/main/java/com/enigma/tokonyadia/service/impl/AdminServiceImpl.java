package com.enigma.tokonyadia.service.impl;

import com.enigma.tokonyadia.entity.Admin;
import com.enigma.tokonyadia.repository.AdminRepository;
import com.enigma.tokonyadia.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    @Override
    public Admin create(Admin admin) {
        log.info("start createAdmin");
        Admin save = adminRepository.save(admin);
        log.info("end createAdmin");
        return save;
    }
}
