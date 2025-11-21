package warehouses.project.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import warehouses.project.model.Product;
import warehouses.project.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Получить все товары с пагинацией.
     */
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * Поиск по названию (с пагинацией)
     */
    public Page<Product> searchProducts(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    /**
     * Найти товар по ID.
     *
     * @throws EntityNotFoundException если не найден
     */
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    }

    /**
     * Создать новый товар.
     */
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Обновить существующий товар.
     */
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getById(id);
        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setUnit(updatedProduct.getUnit());
        return productRepository.save(existing);
    }

    /**
     * Удалить товар по ID.
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
