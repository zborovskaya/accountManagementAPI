package by.zborovskaya.accountManagementAPI.controller;

import by.zborovskaya.accountManagementAPI.dto.TransferRequest;
import by.zborovskaya.accountManagementAPI.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {
        transferService.transferMoney(request);
        return ResponseEntity.ok("Transfer successful");
    }
}

