package ru.kocha.exchanger_v1.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.kocha.exchanger_v1.repository.*;
import ru.kocha.exchanger_v1.repository.connection.DataSource;
import ru.kocha.exchanger_v1.service.CurrencyService;
import ru.kocha.exchanger_v1.service.ExchangeRateService;
import ru.kocha.exchanger_v1.service.ExchangeService;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DataSource dataSource = new DataSource();
        CurrencyRepository currencyRepository = new CurrencyRepositoryImpl();
        ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepositoryImpl();
        UnitOfWork unitOfWork = new UnitOfWorkImpl(dataSource);
        CurrencyService currencyService = new CurrencyService(currencyRepository, unitOfWork);
        ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateRepository, unitOfWork);
        ExchangeService exchangeService = new ExchangeService(exchangeRateRepository, unitOfWork);

        sce.getServletContext().setAttribute("dataSource", dataSource);
        sce.getServletContext().setAttribute("currencyRepository", currencyRepository);
        sce.getServletContext().setAttribute("exchangeRateRepository", exchangeRateRepository);
        sce.getServletContext().setAttribute("unitOfWork", unitOfWork);
        sce.getServletContext().setAttribute("currencyService", currencyService);
        sce.getServletContext().setAttribute("exchangeRateService", exchangeRateService);
        sce.getServletContext().setAttribute("exchangeService", exchangeService);
    }
}
