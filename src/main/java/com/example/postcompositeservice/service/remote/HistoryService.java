package com.example.postcompositeservice.service.remote;

import com.example.postcompositeservice.dto.GeneralResponse;
import com.example.postcompositeservice.dto.History;
import com.example.postcompositeservice.dto.HistoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@FeignClient("history-service")
public interface HistoryService {
    @PostMapping(value = "history-service/histories")
    ResponseEntity<GeneralResponse> setHistory(@RequestBody HistoryRequest req);

    @GetMapping(value = "history-service/histories")
    List<History> getAllHistoriesByUserId();
}
