package com.example.postcompositeservice.service.remote;

import com.example.postcompositeservice.dto.GeneralResponse;
import com.example.postcompositeservice.dto.HistoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient("history-service")
public interface HistoryService {
    @PostMapping(value = "history-service/histories")
    ResponseEntity<GeneralResponse> setHistory(@RequestBody HistoryRequest req);
}
