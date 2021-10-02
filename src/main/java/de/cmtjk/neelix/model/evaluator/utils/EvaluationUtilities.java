package de.cmtjk.neelix.model.evaluator.utils;

import de.cmtjk.neelix.logger.Logger;
import de.cmtjk.neelix.model.resources.Request;

import java.time.Month;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class EvaluationUtilities {

    public Map<Integer, Map<Month, List<Request>>> sortRequestsByDate(List<Request> requestList) {
        Map<Integer, Map<Month, List<Request>>> sortedRequests = new HashMap<>();

        requestList.forEach(request -> {

            if (!sortedRequests.containsKey((request.getDate().getYear()))) {
                sortedRequests.put((request.getDate().getYear()), new HashMap<Month, List<Request>>());
                Logger.getInstance().trace("Request(s) found for year: " + request.getDate().getYear());

            }
            if (!sortedRequests.get((request.getDate().getYear())).containsKey((request.getDate().getMonth()))) {
                sortedRequests.get((request.getDate().getYear())).put((request.getDate().getMonth()),
                        new LinkedList<Request>());
                Logger.getInstance().trace("Request(s) found for month: " + request.getDate().getMonth());
            }

            sortedRequests.get((request.getDate().getYear())).get((request.getDate().getMonth())).add(request);

        });

        Logger.getInstance().trace("Finished!");
        return sortedRequests;

    }

    public Map<Integer, Map<Month, Map<String, AtomicInteger>>> sumOfRequestsSortedByTypeAndDate(
            List<Request> requestList) {
        Map<Integer, Map<Month, Map<String, AtomicInteger>>> sumOfRequestsByDateAndType = new HashMap<>();

        requestList.forEach(request -> {

            if (!sumOfRequestsByDateAndType.containsKey((request.getDate().getYear()))) {
                sumOfRequestsByDateAndType.put((request.getDate().getYear()),
                        new HashMap<Month, Map<String, AtomicInteger>>());
                Logger.getInstance().trace("Request(s) found for year: " + request.getDate().getYear());
            }
            if (!sumOfRequestsByDateAndType.get((request.getDate().getYear()))
                    .containsKey((request.getDate().getMonth()))) {
                sumOfRequestsByDateAndType.get((request.getDate().getYear())).put((request.getDate().getMonth()),
                        new HashMap<>());
                Logger.getInstance().trace("Request(s) found for month: " + request.getDate().getMonth());
            }
            if (!sumOfRequestsByDateAndType.get((request.getDate().getYear())).get(request.getDate().getMonth())
                    .containsKey(request.getRequestType().getName())) {
                sumOfRequestsByDateAndType.get((request.getDate().getYear())).get(request.getDate().getMonth())
                        .put(request.getRequestType().getName(), new AtomicInteger(0));
            }

            sumOfRequestsByDateAndType.get((request.getDate().getYear())).get(request.getDate().getMonth())
                    .get(request.getRequestType().getName()).incrementAndGet();

        });

        return sumOfRequestsByDateAndType;
    }

}
