package controller;

import dao.BrandDAO;
import dao.CategoryDAO;
import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/products")
public class ProductsServlet extends HttpServlet {

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        ProductDAO productDAO = new ProductDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        BrandDAO brandDAO = new BrandDAO();

        String keyword = req.getParameter("keyword");
        int categoryId = parseInt(req.getParameter("categoryId"), 0);
        int brandId = parseInt(req.getParameter("brandId"), 0);
        double minPrice = parseDouble(req.getParameter("minPrice"), -1);
        double maxPrice = parseDouble(req.getParameter("maxPrice"), -1);
        String line = req.getParameter("line");
        String color = req.getParameter("color");
        String sort = req.getParameter("sort");
        int page = Math.max(1, parseInt(req.getParameter("page"), 1));
        int pageSize = 12;

        int totalProducts = productDAO.countProducts(keyword, categoryId, brandId, minPrice, maxPrice, line, color);
        int totalPages = Math.max(1, (int) Math.ceil(totalProducts * 1.0 / pageSize));
        if (page > totalPages) {
            page = totalPages;
        }

        req.setAttribute("products", productDAO.getProducts(keyword, categoryId, brandId, minPrice, maxPrice, line, color, sort, page, pageSize));
        req.setAttribute("categories", categoryDAO.getAll());
        req.setAttribute("brands", brandDAO.getAll());

        req.setAttribute("keyword", keyword);
        req.setAttribute("categoryId", categoryId);
        req.setAttribute("brandId", brandId);
        req.setAttribute("minPrice", minPrice);
        req.setAttribute("maxPrice", maxPrice);
        req.setAttribute("line", line);
        req.setAttribute("color", color);
        req.setAttribute("sort", sort);
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalProducts", totalProducts);

        req.getRequestDispatcher("/products.jsp").forward(req, res);
    }
}